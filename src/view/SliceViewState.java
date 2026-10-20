package view;

import model.Player;

/** ウィンドウごとのXY閲覧層。プレイヤーの位置は変更しない。 */
public final class SliceViewState {
    private final Player player;
    private final int layerCount;
    private Integer selectedLayer;

    public SliceViewState(Player player, int layerCount) {
        this.player = player;
        this.layerCount = layerCount;
    }

    public int layerCount() { return layerCount; }
    public int currentLayer() { return player.position().coordinate(2); }
    public int displayedLayer() { return selectedLayer == null ? currentLayer() : selectedLayer; }

    public void selectLayer(int layer) {
        if (layer < 0 || layer >= layerCount) throw new IllegalArgumentException("Invalid layer.");
        selectedLayer = layer;
    }

    public void followPlayer() { selectedLayer = null; }
}
