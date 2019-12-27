# workflow-net-logger
一个适用性较强的 Workflow-net 业务过程模型日志生成算法

## 问题说明

使用Workflow-net表示的过程模型包含四个元素，库所（Place）、变迁（Transition）、弧线（Arc）和令牌（Token）。没有输入弧线的库所称为输入库所，没有输出弧线的库所称为输出库所。每一个过程模型只能包含一个输入库所和一个输出库所。过程模型每一次运行的初始状态是输入库所中填入一个令牌，结束状态是在输出库所中填入一个令牌。每一个库所有一个或多个输入、输出变迁，每一个变迁有一个或多个输入、输出库所。所有输入库所中都填有令牌时，变迁达到可执行状态；当变迁执行时消耗每一个输入库所中的1个令牌，并在所有输出库所中填入一个新的令牌。过程模型运行的过程是从初始状态开始，所有可执行的变迁都执行一次，最终达到结束状态。将一次运行中所有执行的变迁按照执行顺序记录下来，形成一个变迁的执行序列，称为过程模型的一条日志，并且使用CaseID（可以是整型数值）作为该条日志的唯一标识符。所有能够使得过程模型从开始状态到达结束状态的变迁执行序列构成全局完整的过程日志。

**〈要求〉** 生成输入的业务过程模型包含的所有可能的任务执行序列，其中循环任务执行不超过1次。提交算法源代码，实现接口getLogOfModel(String modelFile, String logFile)。

**〈输入〉**Workflow-net表示的业务过程模型（Business Process Model），XML格式

**〈输出〉**过程日志文件



## 整体流程描述

1. 通过〔Parser〕将模型配置文件解析成WorkflowNet对象，并执行初始化操作（寻找WorkflowNet的输入库所和输出库所；

2. 调用〔WorkflowNet〕的getLogOfGraph方法执行日志生成算法获取业务过程执行日志。



## 核心算法描述

**算法核心**：带〈去环剪枝〉的〈多入口〉深度优先遍历。

1. 初始状态下，将输入库所加入到当前节点列表〔curNodes〕中
2. 递归执行多入口深度优先遍历：
	先对当前入口节点列表〔curNodes〕筛去被其他入口节点可达的部分，再逐一遍历：
		1. 若当前节点〔curNode〕为输出库所，则将路径〔path〕加入到日志集合中，return
		2. 若当前节点出度为0，则跳过该节点
		3. 在〔curNodes〕中删去当前节点
		4. 若当前节点是一个库所，则对它指向的变迁逐一做以下操作：
			1. 若该变迁出度为0，则跳过该变迁
			2. 将该变迁加到〔curNodes〕中
			3. 递归执行深度优先遍历〔getLogOfGraph〕
			4. 在〔curNodes〕中删除该变迁
		5. 若当前节点是一个变迁，则：
			1. 将该变迁加入到当前路径中
			2. 对该变迁指向的库所逐一执行以下操作：
				若库所入度大于1（则可能是环入口），则：若当前可能的环入口集合〔cycleEntries〕没有该库所，则将其加到〔cycleEntries〕中，否则在图的拷贝中去掉当前变迁指向该库所的边
		3. 将所有指向的库所加入到〔curNodes〕中
			4. 递归执行深度优先遍历〔getLogOfGraph〕
			5. 在〔curNodes〕中删去所有指向的库所
			6. 在当前路径中删去当前变迁
		6. 往〔curNodes〕插入当前节点（保留原来的位置）
3. 对结果日志列表进行去重

**【核心问题处理】**

1. 如何避免图中环结构导致的无限循环：对所有入度大于一（可能为环入口）的库所，在第一次访问的时候将其加到“可能的环入口集合”中，若再次访问，则将指向它的这条路径去掉，就不会沿着相同路径访问第二遍了。

   ![image-20191227214050136](https://tva1.sinaimg.cn/large/006tNbRwly1gabm2mnhnfj314u0o2aia.jpg)

   1. 首次访问入度大于一的库所，将其加入可能的环入口列表中
   2. 执行循环体
   3. 该库所存在于可能的环入口列表中，则在图中切断当前路径最后指向该库所的一条边
   4. 下次执行深度优先遍历将不会再执行这条边，即每个循环体只执行最多一次；且由于(4)中上方的变迁没有出度，所以不会加入到日志中（否则遇到并行和循环嵌套时可能出错）


2. 如何处理并行执行和选择执行：采用多入口的深度有限遍历，且对库所和变迁的处理方法不同：对库所的处理类似于传统的深度优先遍历；若遇到变迁，则将其指向的库所都加到当前入口节点中，下一次遍历将随机从入口节点选择一个递归执行该算法。但需要注意的是，需要对当前入口节点进行筛选，去掉被其他节点可达的节点（若一个节点被其他节点可达，则在对其他节点进行操作之前不能对该节点执行递归操作，又因为可达性，故删去该节点）。

   ![image-20191227214145884](https://tva1.sinaimg.cn/large/006tNbRwly1gabm3lp4dij31gk0s21kx.jpg)

   1. 当前〔curNodes〕中只有红色标注的变迁

   2. 由于当前节点为变迁，将三个指向的库所都加入到〔curNodes〕中

   3. 下方并行分支先遍历完毕，到达右方变迁节点（遍历中可能存在的情况，但不符合实际）

   4. 右方变迁节点被其他两个红色标注的库所节点可达，所以将右方变迁节点从〔curNodes〕筛去（若不筛去，则算法可能先遍历右方变迁之后的路径，出错）

      


## 模块分解

本解决方案整体分为两个模块，〔Parser〕和〔Model〕，其中〔Model〕分为〔WorkflowNet〕和〔Node〕，〔Node〕又分为〔Place〕和〔Transition〕。

〔Parser〕使用javax.xml.parsers将模型文件*.pnml解析为〔WorkflowNet〕对象，该部分较为常规，不再赘述

〔Node〕是〔WorkflowNet〕的节点，拥有id和name属性，并实现了equals、hashCode、toString等方法：

```java
class Node {
	String id;
	String name;
	boolean equals(Object object) {}
	int hashCode() {}
}
```

〔Place〕和〔Transition〕继承于〔Node〕，覆盖了toString方法

〔WorkflowNet〕实现Workflow-net模型的核心数据结构和日志生成算法及其依赖的计算入度出度、判断是否可达、去重等方法



## 代码说明

代码主类为Main，故使用前需要将代码文件名修改为Main.java，通过调用Main中的`static void getLogOfModel(String modelFile, String logFile)` 接口即可，如下所示：

```java
public class Client {
    public static void main(String[] args) {
        String modelFile = "src/main/resources/models/Model1.pnml";
        String logFile = "src/main/resources/logs/log1.txt";
        Main.getLogOfModel(modelFile, logFile);
    }
}
```

输出文件格式（方框中为Transition名称），最后一行为生成日志数量。

```
[k] -> [0] -> [2] -> [K] -> [W]
[k] -> [0] -> [2] -> [K] -> [b] -> [E] 
……
[f] -> [2] -> [K] -> [W]
[f] -> [2] -> [a] -> [b] -> [E]
=== All logs fetched: 12 ===
```



## 测试报告

（1）简单分支

![image-20191227214514334](https://tva1.sinaimg.cn/large/006tNbRwly1gabm785xx4j31eu09yjy3.jpg)

执行结果，共产生12条日志

```
[k] -> [0] -> [2] -> [K] -> [W]
[k] -> [0] -> [2] -> [K] -> [b] -> [E]
[k] -> [0] -> [2] -> [a] -> [W]
[k] -> [0] -> [2] -> [a] -> [b] -> [E]
[3] -> [2] -> [K] -> [W]
[3] -> [2] -> [K] -> [b] -> [E]
[3] -> [2] -> [a] -> [W]
[3] -> [2] -> [a] -> [b] -> [E]
[f] -> [2] -> [K] -> [W]
[f] -> [2] -> [K] -> [b] -> [E]
[f] -> [2] -> [a] -> [W]
[f] -> [2] -> [a] -> [b] -> [E]
=== All logs fetched: 12 ===
```

（2）分支 + 并发嵌套循环

![image-20191227214623817](https://tva1.sinaimg.cn/large/006tNbRwly1gabm8f751gj31kk0im13o.jpg)

执行结果，共产生1512条日志

```
[y] -> [V] -> [Z] -> [7] -> [V] -> [j] -> [a] -> [L] -> [z] -> [s] -> [z] -> [L] -> [4] -> [l] -> [r] -> [W]
……
[y] -> [U] -> [V] -> [j] -> [Y] -> [L] -> [z] -> [L] -> [V] -> [4] -> [r] -> [W]
[y] -> [U] -> [V] -> [j] -> [Y] -> [o] -> [9] -> [W]
=== All logs fetched: 1512 ===
```

（3）分支嵌套

![image-20191227214653923](https://tva1.sinaimg.cn/large/006tNbRwly1gabm8xzpyqj31l80fkk1m.jpg)

执行结果，共产生436条日志

```
[V] -> [q] -> [o] -> [3] -> [O]
[V] -> [q] -> [o] -> [6] -> [O]
[B] -> [i] -> [j] -> [l] -> [o] -> [C] -> [h] -> [K] -> [D] -> [3] -> [O]
……
[B] -> [i] -> [Z] -> [f] -> [Q] -> [j] -> [K] -> [D] -> [3] -> [O]
[B] -> [i] -> [Z] -> [f] -> [2] -> [j] -> [K] -> [D] -> [6] -> [O]
[z] -> [3] -> [O] 
=== All logs fetched: 436 ===
```

（4）随机生成的复杂图形

![image-20191227214736049](https://tva1.sinaimg.cn/large/006tNbRwly1gabm9oiwghj317m0u0qv5.jpg)

执行结果，共产生1197条日志

```
[R] -> [aeC] -> [h] -> [I] -> [9] -> [yX] -> [j] -> [rVs] -> [iiL] -> [m] -> [Bb] -> [kLy] -> [8]
[R] -> [aeC] -> [h] -> [I] -> [9] -> [yX] -> [j] -> [rVs] -> [8L]
……
[R] -> [aeC] -> [h] -> [I] -> [9] -> [yX] -> [U] -> [iiL] -> [m] -> [Bb] -> [kLy] -> [8]
[R] -> [aeC] -> [h] -> [I] -> [9] -> [yX] -> [U] -> [8L]
=== All logs fetched: 1197 ===
```

（5）两条循环路径

![image-20191227214840750](https://tva1.sinaimg.cn/large/006tNbRwly1gabmatnbwtj31aq0h41kx.jpg)

执行结果，共产生5条日志

```
[0] -> [1] -> [2] -> [1] -> [3] -> [1] -> [4]
[0] -> [1] -> [2] -> [1] -> [4]
[0] -> [1] -> [3] -> [1] -> [2] -> [1] -> [4]
[0] -> [1] -> [3] -> [1] -> [4]
[0] -> [1] -> [4]
=== All logs fetched: 5 ===
```

（6）并发中带循环

![image-20191227214910514](https://tva1.sinaimg.cn/large/006tNbRwly1gabmbb0le4j31w40ewgv2.jpg)

执行结果，共产生8条日志

```
[jUc] -> [aN] -> [7] -> [hi]
[jUc] -> [aN] -> [7] -> [C]
[jUc] -> [aN] -> [Ib7] -> [7] -> [hi]
[jUc] -> [aN] -> [Ib7] -> [7] -> [C]
[L] -> [aN] -> [7] -> [hi]
[L] -> [aN] -> [7] -> [C]
[L] -> [aN] -> [Ib7] -> [7] -> [hi]
[L] -> [aN] -> [Ib7] -> [7] -> [C]
=== All logs fetched: 8 ===
```

