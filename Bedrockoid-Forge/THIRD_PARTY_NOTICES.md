# Third-party notices

Bedrockoid contains code adapted from the projects below. Attribution that used to live in source comments is kept here so the Java sources stay comment-free.

## SnowUnderTrees by bl4ckscor3

Copyright (c) 2022 bl4ckscor3. MIT License.
https://github.com/bl4ckscor3/SnowUnderTrees

Used in: `common/snow/BedrockSnowManager`, `common/snow/snow_managers/*`, `common/world/features/custom/SnowUnderTreeFeature`.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## Wilder Wild by FrozenBlock

https://github.com/FrozenBlock/WilderWild

Snowlogging logic adapted with renamed members, mapping changes and Bedrockoid config checks:

- `common/utils/BlockUtils` (snowlogging helpers), from `block/impl/SnowloggingUtils.java`
- `mixin/blocks/SnowyBlockMixin`, from `mixin/snowlogging/SnowyDirtBlockMixin.java`
- `mixin/client` block render mixins, from `mixin/snowlogging/client/BlockRenderDispatcherMixin.java`
- `mixin/client/sodium/BlockRendererMixin`, from `mixin/client/sodium/BlockRendererMixin.java`
- `mixin/worldgen/BiomeMixin`, from `mixin/snowlogging/worldgen/BiomeMixin.java`
- `mixin/worldgen/ServerLevelMixin` / `ServerWorldMixin`, from `mixin/snowlogging/worldgen/ServerLevelMixin.java`
- `mixin/entities/ServerPlayerInteractionManagerMixin`, originally from `mixin/snowlogging/ServerPlayerGameModeMixin.java`

## BedrockIfy by juancarloscp52

https://github.com/juancarloscp52/BedrockIfy

Reach-around placement adapted with a texture indicator instead of a fill bar and Bedrockoid config checks:

- `common/utils/ReachPlacementUtils`, from `client/features/reacharoundPlacement/ReachAroundPlacement.java`; ray-cast helper by axialeaa
- `mixin/client/MinecraftClientMixin`, from `mixin/client/features/reacharoundPlacement/MinecraftClientMixin.java`