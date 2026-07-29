package io.github.suel_ki.uei.compat.emi.compat;

/*
 * Adapted from AdvancedLootInfo
 * Original source: https://github.com/yanny7/AdvancedLootInfo/blob/master/ali/common-emi/src/main/java/com/yanny/ali/emi/compatibility/IMouseEvents.java
 * Original Copyright (c) Yanny
 * Licensed under the MIT License.
 */
public interface IMouseEvents {
    boolean onMouseScrolled(double mouseX, double mouseY, double scrollDeltaY);
    boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);
}
