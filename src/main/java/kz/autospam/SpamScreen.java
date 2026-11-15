package kz.autospam;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

public class SpamScreen extends Screen {
    private static final int PW = 280;
    private static final int PH = 170;

    private TextFieldWidget field;
    private int left, top;

    public SpamScreen() {
        super(Text.literal("AutoSpam"));
    }

    private static double toDelay(double v) {
        return Math.round((0.5 + v * 9.5) * 10.0) / 10.0;
    }

    private static Text startLabel() {
        return SpamState.enabled
                ? Text.literal("■ Остановить").styled(s -> s.withColor(0xFF5555))
                : Text.literal("▶ Запустить").styled(s -> s.withColor(0x55FF55));
    }

    @Override
    protected void init() {
        left = (width - PW) / 2;
        top = (height - PH) / 2;

        field = new TextFieldWidget(textRenderer, left + 15, top + 46, PW - 30, 20, Text.empty());
        field.setMaxLength(256);
        field.setPlaceholder(Text.literal("Строчка для писания").styled(s -> s.withColor(0x888888)));
        field.setText(SpamState.message);
        field.setChangedListener(s -> SpamState.message = s);
        addDrawableChild(field);
        setInitialFocus(field);

        addDrawableChild(new SliderWidget(left + 15, top + 76, PW - 30, 20, Text.empty(),
                (SpamState.delay - 0.5) / 9.5) {
            {
                updateMessage();
            }

            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Кулдаун: " + String.format(Locale.ROOT, "%.1f", toDelay(value)) + " сек"));
            }

            @Override
            protected void applyValue() {
                SpamState.delay = toDelay(value);
            }
        });

        addDrawableChild(ButtonWidget.builder(startLabel(), b -> {
            SpamState.toggle();
            b.setMessage(startLabel());
        }).dimensions(left + 15, top + 106, PW - 30, 20).build());
    }

    @Override
    public void renderBackground(DrawContext c, int mx, int my, float delta) {
        super.renderBackground(c, mx, my, delta);
        // тень, рамка и панель
        c.fill(left - 2, top - 2, left + PW + 2, top + PH + 2, 0xFF3A7BFF);
        c.fill(left, top, left + PW, top + PH, 0xEE15151F);
        c.fill(left, top, left + PW, top + 24, 0xFF22223A);
    }

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        super.render(c, mx, my, delta);
        c.drawCenteredTextWithShadow(textRenderer, Text.literal("§l⚡ AutoSpam"), width / 2, top + 8, 0xFFFFFFFF);
        c.drawCenteredTextWithShadow(textRenderer, Text.literal("Напиши что хочешь проспамить в чат сюда"),
                width / 2, top + 32, 0xFFAAAAAA);

        Text status = SpamState.enabled
                ? Text.literal("● Работает").styled(s -> s.withColor(0x55FF55))
                : Text.literal("● Выключено").styled(s -> s.withColor(0xFF5555));
        c.drawCenteredTextWithShadow(textRenderer, status, width / 2, top + 134, 0xFFFFFFFF);
        c.drawCenteredTextWithShadow(textRenderer, Text.literal("Команды начинай с /   •   Закрыть: Esc"),
                width / 2, top + 150, 0xFF777777);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT && !field.isFocused()) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
