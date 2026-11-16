//? if emi {
package io.github.suel_ki.uei.compat.emi.compat;

public interface IMouseEvents {
    boolean onMouseScrolled(double mouseX, double mouseY, double scrollDeltaY);
    boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);
    default void onMouseReleased(double mouseX, double mouseY, int button) {}
}
//?}
