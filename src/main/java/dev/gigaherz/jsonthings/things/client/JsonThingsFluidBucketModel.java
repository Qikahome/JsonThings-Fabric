package dev.gigaherz.jsonthings.things.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import io.github.fabricators_of_create.porting_lib.models.CompositeModel;
import io.github.fabricators_of_create.porting_lib.models.UnbakedGeometryHelper;
import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.models.geometry.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib.models.geometry.SimpleModelState;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Function;

/**
 * 流体桶模型 loader（{@code jsonthings:fluid_bucket}）。
 * <p>
 * 与 PL 的 {@code porting_lib:fluid_container} 等效，但**不依赖 transfer 渲染管线**：
 * 所有贴图都经 ModelBakery 的 {@code spriteGetter}（material 流程，缺失时兜底 missing sprite），
 * 规避 Fabric 烘焙期 atlas 尚未备好时 {@code FluidVariantRendering.getSprite} 的 NPE。
 * <p>
 * 语义：base = 桶壳贴图；fluid = 桶内液体贴图（不染色，对齐 Forge 上游）；流体层以桶壳形状作 mask 裁剪。
 */
public class JsonThingsFluidBucketModel implements IUnbakedGeometry<JsonThingsFluidBucketModel>
{
    /** 内置默认液体剪影：桶内液体区域（16x16，白色不透明部分即液体显示范围）。 */
    private static final ResourceLocation DEFAULT_MASK = new ResourceLocation("jsonthings", "item/fluid_bucket_mask");

    // 与 PL DynamicFluidContainerModel 一致：流体层略外推防止 z-fighting
    private static final Transformation FLUID_TRANSFORM =
            new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.002f), new Quaternionf());

    private JsonThingsFluidBucketModel()
    {
    }

    @Override
    public BakedModel bake(BlockModel context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter,
                           ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation, boolean isGui3d)
    {
        Material baseMaterial = context.hasTexture("base") ? context.getMaterial("base") : null;
        Material fluidMaterial = context.hasTexture("fluid") ? context.getMaterial("fluid") : null;
        // 液体剪影 mask：默认用内置的"桶内液体"形状，资产可在 textures.mask 覆盖
        Material maskMaterial = context.hasTexture("mask")
                ? context.getMaterial("mask")
                : new Material(InventoryMenu.BLOCK_ATLAS, DEFAULT_MASK);

        final TextureAtlasSprite baseSpriteRaw = baseMaterial != null ? spriteGetter.apply(baseMaterial) : null;
        final TextureAtlasSprite fluidSpriteRaw = fluidMaterial != null ? spriteGetter.apply(fluidMaterial) : null;
        final TextureAtlasSprite maskSpriteRaw = spriteGetter.apply(maskMaterial);
        final TextureAtlasSprite baseSprite = baseSpriteRaw != null ? baseSpriteRaw : missingSprite(spriteGetter);
        final TextureAtlasSprite fluidSprite = fluidSpriteRaw != null ? fluidSpriteRaw : missingSprite(spriteGetter);
        final TextureAtlasSprite maskSprite = maskSpriteRaw != null ? maskSpriteRaw : missingSprite(spriteGetter);

        var builder = CompositeModel.Baked.builder(context.hasAmbientOcclusion(), false,
                context.getGuiLight().lightLikeBlock(), baseSprite, overrides, context.getTransforms());

        // 桶壳（正常 item 模型）
        var baseElements = UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite.contents());
        builder.addQuads(UnbakedGeometryHelper.bakeElements(baseElements, $ -> baseSprite, modelState, modelLocation));

        // 流体层：以液体剪影 mask 裁剪，填充流体 still 贴图（不染色 → 白色）
        var maskElements = UnbakedGeometryHelper.createUnbakedItemMaskElements(1, maskSprite.contents());
        var fluidState = new SimpleModelState(modelState.getRotation().compose(FLUID_TRANSFORM), modelState.isUvLocked());
        builder.addQuads(UnbakedGeometryHelper.bakeElements(maskElements, $ -> fluidSprite, fluidState, modelLocation));

        builder.setParticle(baseSprite);
        return builder.build();
    }

    /** 兜底：missing 纹理总是被 atlas 收集，经 spriteGetter 取回可保证非 null。 */
    private static TextureAtlasSprite missingSprite(Function<Material, TextureAtlasSprite> spriteGetter)
    {
        return spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation()));
    }

    public static final class Loader implements IGeometryLoader<JsonThingsFluidBucketModel>
    {
        public static final Loader INSTANCE = new Loader();

        @Override
        public JsonThingsFluidBucketModel read(JsonObject json, JsonDeserializationContext deserializationContext)
        {
            // "fluid" 字段保留为兼容写法；当前渲染不依赖它（贴图由 textures.fluid 指定）
            return new JsonThingsFluidBucketModel();
        }
    }
}
