package slimeknights.tconstruct.library.data.tinkering;

import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Base data generator to generate station slot layouts */
public abstract class AbstractStationSlotLayoutProvider extends GenericDataProvider {

  /** Sort index for weapons */
  protected static final int SORT_WEAPON = 3;
  /** Sort index for harvest */
  protected static final int SORT_HARVEST = 6;
  /** Sort index for ranged */
  protected static final int SORT_RANGED = 8;
  /** Index for large tools, add to either weapon or harvest */
  protected static final int SORT_LARGE = 6;
  /** Index for armor */
  protected static final int SORT_ARMOR = 15;

  private final Map<ResourceLocation,StationSlotLayout.Builder> allLayouts = new HashMap<>();

  public AbstractStationSlotLayoutProvider(DataGenerator generator) {
    super(generator, PackType.SERVER_DATA, StationSlotLayoutLoader.FOLDER, StationSlotLayoutLoader.GSON);
  }

  /**
   * Function to add all relevant layouts
   */
  protected abstract void addLayouts();

  /** Defines the given ID as a general layout */
  protected StationSlotLayout.Builder define(ResourceLocation id) {
    return allLayouts.computeIfAbsent(id, i -> StationSlotLayout.builder());
  }

  /** Defines the given ID as a item layout */
  protected StationSlotLayout.Builder define(ItemLike item) {
    return define(Registry.ITEM.getKey(item.asItem()));
  }

  /** Defines the given ID as a tool layout, sets icon and name */
  protected StationSlotLayout.Builder defineModifiable(IModifiableDisplay item) {
    return define(Registry.ITEM.getKey(item.asItem()))
      .translationKey(item.asItem().getDescriptionId())
      .icon(item.getRenderTool());
  }

  /** Defines the given ID as a tool layout, sets icon and name */
  protected StationSlotLayout.Builder defineModifiable(Supplier<? extends IModifiableDisplay> item) {
    return defineModifiable(item.get());
  }

  @Override
  public void run(CachedOutput cache) throws IOException {
    addLayouts();
    allLayouts.forEach((id, builder) -> saveJson(cache, id, builder.build()));
  }
}
