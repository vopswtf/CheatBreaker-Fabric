package cc.vops.cheatbreaker.client.ui.overlay;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.ui.AbstractGui;
import cc.vops.cheatbreaker.client.ui.mainmenu.AbstractElement;
import cc.vops.cheatbreaker.client.ui.overlay.element.ElementListElement;
import cc.vops.cheatbreaker.client.ui.overlay.element.FlatButtonElement;
import cc.vops.cheatbreaker.client.ui.overlay.element.MessagesElement;
import cc.vops.cheatbreaker.client.ui.overlay.element.RadioElement;
import cc.vops.cheatbreaker.client.ui.overlay.friend.*;
import cc.vops.cheatbreaker.client.util.*;
import cc.vops.cheatbreaker.client.util.font.Fonts;
import cc.vops.cheatbreaker.client.util.friend.Friend;
import cc.vops.cheatbreaker.client.util.friend.Status;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.util.*;

@Getter
public class SocialOverlayScreen extends AbstractGui {
    private static SocialOverlayScreen instance;
    private final FriendsListElement friendsListElement;
    private final FriendRequestListElement friendRequestsElement;
    private final FlatButtonElement friendsButton;
    private final FlatButtonElement requestsButton;
    private ElementListElement<?> selectedElement;
    private final RadioElement radioElement;
    private long initGuiMillis = 0L;
    private final Queue<Alert> alertQueue = new LinkedList<>();
    private final List<Alert> alertList = new ArrayList<>();
    private long revertToContextTime;
    public static Screen previousScreen; // gonna render this under lol

    public SocialOverlayScreen() {
        List<FriendElement> arrayList = new ArrayList<>();
        CheatBreaker.getInstance()
                .getFriendsManager()
                .getFriends()
                .forEach((string, friend) -> arrayList.add(new FriendElement(friend)));
        AbstractElement[] elements = new AbstractElement[5];
        elements[0] = (this.friendsListElement = new FriendsListElement(arrayList));
        elements[1] = (this.friendRequestsElement = new FriendRequestListElement(new ArrayList<>()));
        elements[2] = (this.requestsButton = new FlatButtonElement("REQUESTS"));
        elements[3] = (this.friendsButton = new FlatButtonElement("FRIENDS"));
        elements[4] = (this.radioElement = new RadioElement());
        this.setElementsAndUpdateSize(elements);
        this.selectedElement = this.friendsListElement;
        this.drawBackground = true;
    }

    public static Optional<SocialOverlayScreen> getInstanceOpt() {
        if (instance == null) return Optional.empty();
        return Optional.of(instance);
    }

    public static SocialOverlayScreen getInstance() {
        if (instance == null) {
            instance = new SocialOverlayScreen();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = new SocialOverlayScreen();
    }

    public void renderGameOverlay(GuiGraphicsExtractor gfx) {
        this.alertList.forEach(alert -> alert.drawAlert(gfx));
    }

    public void setMessages(Friend friend) {
        try {
            MessagesElement messagesElement2 = null;
            for (AbstractElement element : this.elements) {
                if (!(element instanceof MessagesElement)) continue;
                messagesElement2 = (MessagesElement)element;
            }
            if (messagesElement2 == null) {
                MessagesElement messagesElement = new MessagesElement(friend);
                this.elements.add(messagesElement);
                messagesElement.setElementSize(170f, 30f, 245f, 150f);
            } else {
                this.elements.add(this.elements.remove(this.elements.indexOf(messagesElement2)));
                messagesElement2.setFriend(friend);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void initMenu() {
        this.friendsButton.setElementSize(0.0f, 28f, 96.976746f * 0.71666664f, 20);
        float f = (float) 28 + this.friendsButton.getHeight() + 1.0f;
        this.initGuiMillis = System.currentTimeMillis();
        this.requestsButton.setElementSize(55.315384f * 1.2745098f, (float) 28, 0.5588235f * 124.36842f, 20);
        float f2 = 190;
        this.friendsListElement.setElementSize(0.0f, f, 140f, this.getScaledHeight() - f);
        this.friendRequestsElement.setElementSize(0.0f, f, 140f, this.getScaledHeight() - f);

        // only set radio element if its off screen or not in the right side
        if (this.radioElement.getY() > this.getScaledHeight() || this.radioElement.getX() > this.getScaledWidth() || this.radioElement.getX() + this.radioElement.getWidth() < this.getScaledWidth() - f2) {
            this.radioElement.setElementSize(this.getScaledWidth() - f2 - (float) 20, (float) 20, f2, 28);
        }
    }

    @Override
    public void resize(int width, int height) {
        try {
            if (previousScreen != null) {
                previousScreen.resize(width, height);
            }
            this.alertList.forEach(Alert::resize);
            this.alertQueue.forEach(Alert::resize);

            super.resize(width, height);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float delta) {
        if (previousScreen != null) {
            gfx.pose().pushMatrix();
            previousScreen.extractRenderStateWithTooltipAndSubtitles(gfx, -1, -1, delta);
            gfx.pose().popMatrix();
        }

        super.extractRenderState(gfx, mouseX, mouseY, delta);
    }

    @Override
    public void onScroll(GuiGraphicsExtractor gfx, int delta) {
        super.onScroll(gfx, delta);
        this.selectedElement.handleScroll(gfx, delta);

        for (AbstractElement element : this.elements) {
            element.handleScroll(gfx, delta);
        }
    }

    @Override
    public void drawMenu(GuiGraphicsExtractor gfx, float mouseX, float mouseY, float delta) {
//        GL11.glClear(256)

        // Background
        RenderUtil.drawRect(gfx, 0.0f, 0.0f, this.getScaledWidth(), this.getScaledHeight(), CheatBreaker.getColor(0.0f, 0.0f, 0.0f, 0.3f));

        RenderUtil.drawRect(gfx, 0.0f, 0.0f, 140, this.getScaledHeight(), -14671840);
        RenderUtil.drawRect(gfx, 140, 0.0f, 141, this.getScaledHeight(), -15395563);
        RenderUtil.drawRect(gfx, 0.0f, 0.0f, 140, 28, -15395563);
        RenderUtil.drawRect(gfx, 6, 6, 22, 22, Friend.getStatusColor(CheatBreaker.getInstance().getStatus()));
//        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Identifier headLocation = PlayerHeads.getHeadLocation(this.mc.getUser().getName(), this.mc.getUser().getProfileId());
        RenderUtil.drawIcon(gfx, headLocation, 7f, 7f, 7f);
        String username = this.mc.getUser().getName();

        RenderUtil.drawString(gfx, Fonts.playRegular16, username, 28f, 5f, -1);
        RenderUtil.drawString(gfx, Fonts.playRegular16, CheatBreaker.getInstance().getStatusString(), 28f, 14f, -5460820);

        boolean statusHovered = mouseX > 6f && mouseX < 94f && mouseY > 6f && mouseY < 22f;
        if (this.isMouseHovered(this.friendsButton, mouseX, mouseY) && statusHovered && CheatBreaker.getInstance().getAssetsWebSocket().isOpen()) {
            RenderUtil.drawRect(gfx, 22, 0.0f, 140, 28, -15395563);
            RenderUtil.drawRect(gfx, 24, 6, 40, 22, Friend.getStatusColor(Status.ONLINE));
            RenderUtil.drawRect(gfx, 42, 6, 58, 22, Friend.getStatusColor(Status.AWAY));
            RenderUtil.drawRect(gfx, 60, 6, 76, 22, Friend.getStatusColor(Status.BUSY));
            RenderUtil.drawRect(gfx, 78, 6, 94, 22, Friend.getStatusColor(Status.HIDDEN));
            boolean onlineHovered = mouseX > 24f && mouseX < 40f;
            boolean awayHovered = mouseX > 42f && mouseX < 58f;
            boolean busyHovered = mouseX > 60f && mouseX < 76f;
            boolean offlineHovered = mouseX > 78f && mouseX < 94f;
            RenderUtil.drawIcon(gfx, headLocation, (float)7, (float)25, (float)7, CheatBreaker.getColor(onlineHovered ? 0.35f : 0.15f, onlineHovered ? 0.35f :0.15f, onlineHovered ? 0.35f :0.15f, 1.0f));
            RenderUtil.drawIcon(gfx, headLocation, (float)7, (float)43, (float)7, CheatBreaker.getColor(awayHovered ? 0.35f : 0.15f, awayHovered ? 0.35f :0.15f, awayHovered ? 0.35f :0.15f, 1.0f));
            RenderUtil.drawIcon(gfx, headLocation, (float)7, (float)61, (float)7, CheatBreaker.getColor(busyHovered ? 0.35f : 0.15f, busyHovered ? 0.35f :0.15f, busyHovered ? 0.35f :0.15f, 1.0f));
            RenderUtil.drawIcon(gfx, headLocation, (float)7, (float)79, (float)7, CheatBreaker.getColor(offlineHovered ? 0.35f : 0.15f, offlineHovered ? 0.35f :0.15f, offlineHovered ? 0.35f :0.15f, 1.0f));
        }
        this.selectedElement.drawElement(gfx, mouseX, mouseY, this.isMouseHovered(this.requestsButton, mouseX, mouseY));
        RenderUtil.drawRect(gfx, 69.5f, 28, 70.5f, (float)28 + this.friendsButton.getHeight(), -14869219);
        RenderUtil.drawRect(gfx, 0.0f, (float)28 + this.friendsButton.getHeight(), 140, (float)28 + this.friendsButton.getHeight() + 1.0f, -15395563);
        this.drawElements(gfx, mouseX, mouseY, this.friendsListElement, this.friendRequestsElement);
    }

    @Override
    protected boolean onMouseClicked(double f, double f2, int n) {
        this.selectedElement.handleElementMouseClicked((float) f, (float) f2, n, this.isMouseHovered(this.requestsButton, (float) f, (float) f2));

        boolean bl2 = this.isMouseHovered(this.friendsButton, (float) f, (float) f2);
        if (bl2 && this.friendsButton.isMouseInside(f, f2) && this.selectedElement != this.friendsListElement) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            this.selectedElement = this.friendsListElement;
        } else if (this.requestsButton.isMouseInside(f, f2) && this.selectedElement != this.friendRequestsElement) {
            CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            this.selectedElement = this.friendRequestsElement;
        }
        boolean bl = f > 6f && f < 134f && f2 > 6f && f2 < 22f;
        if (bl2 && bl && CheatBreaker.getInstance().getAssetsWebSocket().isOpen()) {
            boolean onlineHovered = f > 24f && f < 40f;
            boolean awayHovered = f > 42f && f < 58f;
            boolean busyHovered = f > 60f && f < 76f;
            boolean offlineHovered = f > 78f && f < 94f;
            if (onlineHovered) {
                CheatBreaker.getInstance().setStatus(Status.ONLINE);
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            } else if (awayHovered) {
                CheatBreaker.getInstance().setStatus(Status.AWAY);
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            } else if (busyHovered) {
                CheatBreaker.getInstance().setStatus(Status.BUSY);
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            } else if (offlineHovered) {
                CheatBreaker.getInstance().setStatus(Status.HIDDEN);
                CheatBreaker.getInstance().setLastOnline(System.currentTimeMillis());
                CheatBreaker.playSound(SoundEvents.UI_BUTTON_CLICK);
            }
            CheatBreaker.getInstance().getAssetsWebSocket().updateClientStatus();
        }
        this.onMouseClicked((float) f, (float) f2, n, this.friendsListElement, this.friendRequestsElement);

        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_GRAVE_ACCENT && CheatBreaker.getInstance().isConsoleAllowed()) {
            boolean shouldOpen = true;
            for (AbstractElement element : this.elements) {
                if (!(element instanceof ConsoleElement)) continue;
                shouldOpen = false;
            }
            if (shouldOpen) {
                ConsoleElement consoleGui = new ConsoleElement();
                this.addElements(consoleGui);
                consoleGui.setElementSize((float)60, (float)30, (float)300, 145);
            }
        }

        return super.keyPressed(event);
    }

    @Override
    protected void onMouseReleased(double mx, double my, int button) {

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void setSection(String string) {
        this.queueAlert("", string);
    }


    public void pollNotifications() {
        this.alertList.removeIf(Alert::shouldDisplay);
        if (this.alertQueue.isEmpty()) {
            return;
        }
        boolean bl = true;
        for (Alert cBAlert2 : this.alertList) {
            if (cBAlert2.isFading()) continue;
            bl = false;
        }
        if (bl) {
            Alert cBAlert3 = this.alertQueue.poll();
            cBAlert3.setMaxHeight(this.getScaledHeight() - (float)Alert.getHeight());
            this.alertList.forEach(cBAlert -> cBAlert.setMaxHeight(cBAlert.getMaxHeight() - (float)Alert.getHeight()));
            this.alertList.add(cBAlert3);
        }
    }


    public void queueAlert(String string, String string2) {
        final String[] string2Final = {string2};
        Minecraft.getInstance().execute(() -> {
            int n = Alert.getWidth();
//            string2Final[0] = ChatColor.formatText(Fonts.playRegular16, string2, n - 10);
            string2Final[0] = string2;
            Alert alert = new Alert(string, string2.split("\n"));
            alert.showTitleBar(string.equals(""));
            this.alertQueue.add(alert);
        });

    }

    public void handleFriend(Friend friend, boolean add) {
        if (add) {
            this.friendsListElement.getElements().add(new FriendElement(friend));
        } else {
            this.friendsListElement.getElements().removeIf(friendElement -> friendElement.getFriend() == friend);
        }
        this.friendsListElement.updateSize();
    }

    public void handleFriendRequest(FriendRequest friendRequest, boolean add) {
        if (add) {
            this.friendRequestsElement.getElements().add(new FriendRequestElement(friendRequest));
        } else {
            this.friendRequestsElement.getElements().removeIf(friendRequestElement -> friendRequestElement.getFriendRequest() == friendRequest);
        }
        this.friendRequestsElement.resetSize();
    }


    @Override
    public void repositionElements() {

    }
}
