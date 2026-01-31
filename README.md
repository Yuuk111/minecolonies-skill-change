# MineColonies Skill Change Addon

提供一个物品，用于对准并调整 MineColonies 公民的技能等级。

## 重要说明
- 本工程使用 MineColonies Maven 依赖。若无法解析，请在 `gradle.properties` 中调整 `minecolonies_maven_group` 与 `minecolonies_version`。
- 物品使用时会向服务端发送消息并尝试调用 MineColonies 技能处理方法。如果 MineColonies API 变更，需要更新 `MineColoniesBridge` 里的反射方法名。

## 使用方式
- 将物品拿在手上右键点击公民：提升选中的技能（蹲下则降低）。
- 右键空气切换技能（蹲下则反向切换）。

## 开发
- 使用 Java 17。
- Forge 1.20.1。
