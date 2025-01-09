# 语言

所有代码注释使用中文，所有提交消息使用英文，除专有名词外都使用小写，由 [CHANGELOG.md](CHANGELOG.md) 显示提交消息中文。

# 提交消息

## 固定消息

- `initialize`：项目初始化
- `update dependencies`：更新依赖

## 其他格式

`类型(平台): 内容`

其中`(平台)`可选，`平台`可选值为`android`、`ios`、`desktop`（Windows、MacOS、Linux），若不指定平台则默认为全平台。

冒号与内容间有空格。

类型：

- `add`：**添加**内容
- `modify`：**修改**内容
- `remove`：**移除**内容
- `release`：**版本发行**，消息内容为版本（`x.x.x`）

# UI

以 8dp 为基准，工具栏小图标使用 4dp。

推荐宽高比：

- 16:9
- 3:2、2:3
- 4:3、3:4
- 1:1