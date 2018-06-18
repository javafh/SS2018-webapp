package de.hsflensburg.java.gwt.client;

import java.util.function.Consumer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import de.hsflensburg.java.gwt.shared.WebAppService;
import de.hsflensburg.java.gwt.shared.WebAppServiceAsync;

import static de.hsflensburg.java.gwt.shared.JsonRpcCommand.call;

import gwt.material.design.client.base.AbstractSideNav;
import gwt.material.design.client.constants.ButtonSize;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.FooterType;
import gwt.material.design.client.constants.IconPosition;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.Position;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialCollapsible;
import gwt.material.design.client.ui.MaterialCollapsibleBody;
import gwt.material.design.client.ui.MaterialCollapsibleHeader;
import gwt.material.design.client.ui.MaterialCollapsibleItem;
import gwt.material.design.client.ui.MaterialContainer;
import gwt.material.design.client.ui.MaterialFAB;
import gwt.material.design.client.ui.MaterialFABList;
import gwt.material.design.client.ui.MaterialFooter;
import gwt.material.design.client.ui.MaterialFooterCopyright;
import gwt.material.design.client.ui.MaterialHeader;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialNavBar;
import gwt.material.design.client.ui.MaterialNavBrand;
import gwt.material.design.client.ui.MaterialNavSection;
import gwt.material.design.client.ui.MaterialSearch;
import gwt.material.design.client.ui.MaterialSideNavDrawer;
import gwt.material.design.client.ui.MaterialTooltip;

/***************************************
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebApp implements EntryPoint
{
	// The web application service
	private final WebAppServiceAsync aWebAppService = GWT.create(
		WebAppService.class);

	private MaterialLabel	aBlockNumberLabel;
	private MaterialLabel	aErrorLabel;

	private MaterialCollapsible aBlockList;

	/***************************************
	 * The entry point method that will be invoked when the application is
	 * invoked in the client browser.
	 */
	@Override
	public void onModuleLoad()
	{
		buildUserInterface();

		updateDisplay();
		Scheduler.get().scheduleFixedDelay(this::updateDisplay, 60000);
	}

	/********************************************
	 * Build the main user interface.
	 */
	private void buildUserInterface()
	{
		HTMLPanel aSitePanel = new HTMLPanel("");

		aSitePanel.add(createHeader());
		aSitePanel.add(createSideMenu());
		aSitePanel.add(createContent());
		aSitePanel.add(createGlobalButton());
		aSitePanel.add(createFooter());

		RootPanel.get().add(aSitePanel);
	}

	private Widget createContent()
	{
		MaterialContainer aContent = new MaterialContainer();

		aBlockNumberLabel = new MaterialLabel();
		aErrorLabel = new MaterialLabel();
		aBlockList = new MaterialCollapsible();

		aContent.setPaddingTop(32);

		aContent.add(new MaterialLabel("Latest Block: "));
		aContent.add(aBlockNumberLabel);
		aContent.add(aErrorLabel);
		aContent.add(aBlockList);

		return aContent;
	}

	private Widget createFooter()
	{
		MaterialFooter aFooter = new MaterialFooter();
		MaterialFooterCopyright aCopyright = new MaterialFooterCopyright();

		aCopyright.add(new MaterialLabel("© 2018 by Nobody"));
		aFooter.add(aCopyright);
		aFooter.setType(FooterType.FIXED);

		return aFooter;
	}

	private Widget createGlobalButton()
	{
		MaterialFAB aFAB = new MaterialFAB();
		MaterialFABList aFabList = new MaterialFABList();

		MaterialButton aFloatingButton = new MaterialButton("", IconType.ADD,
			ButtonType.FLOATING);
		MaterialButton aButton1 = new MaterialButton("", IconType.PALETTE,
			ButtonType.FLOATING);
		MaterialButton aButton2 = new MaterialButton("", IconType.MAIL,
			ButtonType.FLOATING);

		aFloatingButton.setSize(ButtonSize.LARGE);
		aFloatingButton.setBackgroundColor(Color.BLUE);
		aButton1.setBackgroundColor(Color.AMBER);
		aButton2.setBackgroundColor(Color.RED);

		aFabList.add(withTooltip(aButton1, "Function 1", Position.LEFT));
		aFabList.add(withTooltip(aButton2, "Function 2", Position.LEFT));

		aFAB.add(
			withTooltip(aFloatingButton, "Global Functions", Position.LEFT));
		aFAB.add(aFabList);

		return aFAB;
	}

	private Widget createHeader()
	{
		MaterialHeader aHeader = new MaterialHeader();
		MaterialNavBar aTopMenu = new MaterialNavBar();
		MaterialNavSection aMenuSection = new MaterialNavSection();
		MaterialLink aSearchLink = new MaterialLink(IconType.SEARCH);

		MaterialNavBar aSearchMenu = new MaterialNavBar();
		MaterialSearch aSearchField = new MaterialSearch("Search");

		aTopMenu.add(new MaterialNavBrand("WebApp"));
		aTopMenu.add(aMenuSection);
		aTopMenu.setActivates("sideMenu");

		aMenuSection.setFloat(Float.RIGHT);
		aMenuSection.add(new MaterialLink("Menu 1"));
		aMenuSection.add(new MaterialLink("Menu 2"));
		aMenuSection.add(new MaterialLink("Menu 3"));
		aMenuSection.add(withTooltip(aSearchLink, "Search", Position.BOTTOM));

		aSearchLink.setIconPosition(IconPosition.NONE);
		aSearchLink.addClickHandler(e -> aSearchField.open());
		aSearchField.addOpenHandler(e -> {
			aTopMenu.setVisible(false);
			aSearchMenu.setVisible(true);
		});

		aSearchField.addCloseHandler(e -> {
			aTopMenu.setVisible(true);
			aSearchMenu.setVisible(false);
		});

		aSearchMenu.add(aSearchField);
		aSearchMenu.setBackgroundColor(Color.WHITE);
		aSearchMenu.setVisible(false);

		aHeader.add(aTopMenu);
		aHeader.add(aSearchMenu);

		return aHeader;
	}

	private AbstractSideNav createSideMenu()
	{
		AbstractSideNav aSideMenu = new MaterialSideNavDrawer();

		aSideMenu.setId("sideMenu");

		aSideMenu.add(new MaterialLink("Side Menu 1"));
		aSideMenu.add(new MaterialLink("Side Menu 2"));
		aSideMenu.add(new MaterialLink("Side Menu 3"));
		aSideMenu.add(new MaterialLink("Side Menu 4"));

		return aSideMenu;
	}

	private void displayBlocks(int nBlockNumber)
	{
		aBlockList.clear();

		for (int i = 0; i < 5; i++)
		{
			MaterialCollapsibleItem aItem = new MaterialCollapsibleItem();
			MaterialCollapsibleHeader aHeader = new MaterialCollapsibleHeader();
			MaterialCollapsibleBody aBody = new MaterialCollapsibleBody();
			aItem.add(aHeader);
			aItem.add(aBody);
			aBlockList.add(aItem);

			String sParams = "[\"0x" + Integer.toHexString(nBlockNumber - i)
				+ ",true]";

			aWebAppService.executeCommand(call("eth_getBlockByNumber", sParams),
				new CommandResultHandler(sJson -> {
					aHeader.add(new MaterialLabel(sJson));
					aBody.add(new MaterialLabel(sJson));
				}));

		}
	}

	private void handleServerError(Throwable rCaught)
	{
		aErrorLabel.setText("ERROR: " + rCaught.getMessage());
	}

	private void updateBlockNumber(String sResponse)
	{
		JSONValue aJsonResult = JSONParser.parseStrict(sResponse);

		if (aJsonResult instanceof JSONString)
		{
			String sBlockNumber = ((JSONString) aJsonResult).stringValue();
			int nBlockNumber = Integer.valueOf(sBlockNumber.substring(2), 16);

			aBlockNumberLabel.setText(nBlockNumber + " (" + sBlockNumber + ")");

			// displayBlocks(nBlockNumber);
		}
		else
		{
			aErrorLabel.setText("Unknown block number format: " + aJsonResult);
		}

	}

	/********************************************
	 * Update the displayed values from the current blockchain state.
	 */
	private boolean updateDisplay()
	{
		aWebAppService.executeCommand(call("eth_blockNumber", null),
			new CommandResultHandler(this::updateBlockNumber));

		return true;

	}

	private IsWidget withTooltip(Widget rWidget, String sTooltip,
		Position ePosition)
	{
		MaterialTooltip aTooltip = new MaterialTooltip(rWidget, sTooltip);

		aTooltip.setPosition(ePosition);

		return aTooltip;
	}

	private class CommandResultHandler implements AsyncCallback<String>
	{
		private Consumer<String> fProcessResponse;

		public CommandResultHandler(Consumer<String> fProcessResponse)
		{
			this.fProcessResponse = fProcessResponse;
		}

		@Override
		public void onFailure(Throwable rCaught)
		{
			handleServerError(rCaught);

		}

		@Override
		public void onSuccess(String sResponse)
		{
			fProcessResponse.accept(sResponse);

		}
	}
}
