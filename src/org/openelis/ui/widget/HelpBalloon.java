package org.openelis.ui.widget;

import org.openelis.ui.resources.ToolTipCSS;
import org.openelis.ui.resources.UIResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class HelpBalloon extends Composite {

	@UiTemplate("Balloon.ui.xml")
	interface BalloonUiBinder extends UiBinder<AbsolutePanel, HelpBalloon> {
	}

	public static final BalloonUiBinder uiBinder = GWT
			.create(BalloonUiBinder.class);

	@UiField
	AbsolutePanel tip, nub, content;

	protected double offset = 50;

	public enum Placement {
		TOP, BOTTOM, RIGHT, LEFT, MOUSE
	}

	protected Placement placement = Placement.TOP;

	protected ToolTipCSS css = UIResources.INSTANCE.toolTip();
	protected PopupPanel pop;

	public HelpBalloon() {
		initWidget(uiBinder.createAndBindUi(this));
		css.ensureInjected();
		nub.getElement().getStyle().setPosition(Position.ABSOLUTE);
		pop = new PopupPanel(true);

		pop.setWidget(this);
	}

	public void setText(String text) {
		this.content.clear();
		content.getElement().setInnerText(text);
	}

	public void setContent(Widget content) {
		this.content.clear();
		this.content.getElement().setInnerText("");
		this.content.add(content);
	}

    public void setPlacement(final Placement placement) {

        this.placement = placement;

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                switch (placement) {
                    case TOP:
                    case MOUSE:
                        tip.setStyleName(css.ToolTip());
                        tip.addStyleName(css.TipBottom());
                        nub.setStyleName(css.Nub());
                        nub.addStyleName(css.NubBottom());
                        break;
                    case BOTTOM:
                        tip.setStyleName(css.ToolTip());
                        tip.addStyleName(css.TipTop());
                        nub.setStyleName(css.Nub());
                        nub.addStyleName(css.NubTop());
                        break;
                    case LEFT:
                        tip.setStyleName(css.ToolTip());
                        tip.addStyleName(css.TipRight());
                        nub.setStyleName(css.Nub());
                        nub.addStyleName(css.NubRight());
                        break;
                    case RIGHT:
                        tip.setStyleName(css.ToolTip());
                        tip.addStyleName(css.TipLeft());
                        nub.setStyleName(css.Nub());
                        nub.addStyleName(css.NubLeft());
                        break;
                }

            }
        });

    }

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public void show(Widget widget, String text) {
		setText(text);
		show(widget);
	}

	public void show(Widget widget, Widget content) {
		setContent(content);
		show(widget);
	}

	private void show(final Widget widget) {
		switch (placement) {
		case TOP:
		case BOTTOM:
		case MOUSE:
			nub.getElement().getStyle().setLeft(offset, Unit.PCT);
			nub.getElement().getStyle().clearTop();
			break;
		case LEFT:
		case RIGHT:
			nub.getElement().getStyle().setTop(offset, Unit.PCT);
			nub.getElement().getStyle().clearLeft();
			break;
		}

		nub.getElement().getStyle().setPosition(Position.ABSOLUTE);

		pop.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int top = -1, left = -1;

				switch (placement) {
				case TOP:
					top = widget.getAbsoluteTop() - offsetHeight;
					left = widget.getAbsoluteLeft() - (offsetWidth / 2)
							+ (widget.getOffsetWidth() / 2);
					break;
				case BOTTOM:
					top = widget.getAbsoluteTop() + widget.getOffsetHeight();
					left = widget.getAbsoluteLeft() - (offsetWidth / 2)
							+ (widget.getOffsetWidth() / 2);
					break;
				case RIGHT:
					top = widget.getAbsoluteTop() - (offsetHeight / 2)
							+ (widget.getOffsetHeight() / 2);
					left = widget.getAbsoluteLeft() + widget.getOffsetWidth();
					break;
				case LEFT:
					top = widget.getAbsoluteTop() - (offsetHeight / 2)
							+ (widget.getOffsetHeight() / 2);
					left = widget.getAbsoluteLeft() - offsetWidth;
					break;
				case MOUSE:
					// top = y - offsetHeight;
					// left = x - (offsetWidth / 2);
				}

				pop.setPopupPosition(left, top);
			}
		});

	}

}
