/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick;

import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.UserVariableBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.dialogs.NewDataDialog;
import org.catrobat.catroid.ui.fragment.FormulaEditorDataFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class ChangeVariableTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final String TAG = ChangeVariableTest.class.getSimpleName();

	private static final int MAX_ITERATIONS = 10;
	private Project project;
	private ChangeVariableBrick changeVariableBrick;

	public ChangeVariableTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testCreateNewUserVariableAndDeletion() {
		String userVariableName = "testVariable1";
		String secondUserVariableName = "testVariable2";

		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getDefaultScene().getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.brick_change_variable)));

		solo.clickOnText(getInstrumentation().getTargetContext().getString(
				R.string.brick_variable_spinner_create_new_variable));
		assertTrue("NewVariableDialog not visible", solo.waitForFragmentByTag(NewDataDialog.DIALOG_FRAGMENT_TAG));

		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_data_name_edit_text);
		solo.enterText(editText, userVariableName);
		solo.clickOnButton(solo.getString(R.string.ok));
		assertTrue("ScriptFragment not visible", solo.waitForText(solo.getString(R.string.brick_change_variable)));
		assertTrue("Created Variable not set in spinner", solo.searchText(userVariableName));

		UserVariable userVariable = (UserVariable) Reflection.getPrivateField(UserVariableBrick.class, changeVariableBrick, "userVariable");
		assertNotNull("UserVariable is null", userVariable);

		solo.clickOnView(solo.getView(R.id.change_variable_spinner));
		solo.waitForText(getInstrumentation().getTargetContext().getString(
				R.string.brick_variable_spinner_create_new_variable));
		solo.clickOnText(getInstrumentation().getTargetContext().getString(
				R.string.brick_variable_spinner_create_new_variable));

		assertTrue("NewVariableDialog not visible", solo.waitForFragmentByTag(NewDataDialog.DIALOG_FRAGMENT_TAG));

		editText = (EditText) solo.getView(R.id.dialog_formula_editor_data_name_edit_text);
		solo.enterText(editText, secondUserVariableName);
		solo.clickOnButton(solo.getString(R.string.ok));
		assertTrue("ScriptFragment not visible", solo.waitForText(solo.getString(R.string.brick_change_variable)));
		assertTrue("Created Variable not set in spinner", solo.searchText(secondUserVariableName));

		userVariable = (UserVariable) Reflection.getPrivateField(UserVariableBrick.class, changeVariableBrick, "userVariable");
		assertNotNull("UserVariable is null", userVariable);
		assertTrue("UserVariable Name not as expected", userVariable.getName().equals(secondUserVariableName));

		solo.clickOnView(solo.getView(R.id.brick_change_variable_edit_text));
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("Data Fragment not shown", solo.waitForFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG));

		solo.clickLongOnText(secondUserVariableName);
		assertTrue("Delete not shown", solo.waitForText(solo.getString(R.string.delete)));
		solo.clickOnText(solo.getString(R.string.delete));
		assertTrue("Data Fragment not shown", solo.waitForFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG));

		solo.goBack();
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		assertTrue("Variable not set in spinner after deletion", solo.searchText(userVariableName));
		Spinner userVariableSpinner = (Spinner) UiTestUtils.getViewContainerByIds(solo, R.id.change_variable_spinner,
				R.id.formula_editor_brick_space);
		assertEquals("UserVariable count not as expected in spinner", 2, userVariableSpinner.getAdapter().getCount());

		solo.goBack();
		assertTrue("ScriptFragment not visible", solo.waitForFragmentByTag(ScriptFragment.TAG));
		assertTrue("Variable not set in spinner after deletion", solo.searchText(userVariableName));
		userVariableSpinner = (Spinner) solo.getView(R.id.change_variable_spinner);
		assertEquals("UserVariable count not as expected in spinner", 2, userVariableSpinner.getAdapter().getCount());
		userVariable = (UserVariable) Reflection.getPrivateField(UserVariableBrick.class, changeVariableBrick, "userVariable");
		assertNotNull("UserVariable is null", userVariable);
		assertTrue("UserVariable Name not as expected", userVariable.getName().equals(userVariableName));
	}

	public void testCreateUserVariableInFormulaEditor() {
		String userVariableName = "testVariable1";

		solo.clickOnView(solo.getView(R.id.brick_change_variable_edit_text));
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("Data Fragment not shown", solo.waitForFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG));

		solo.clickOnView(solo.getView(R.id.button_add));
		assertTrue("Add Data Dialog not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_data_dialog_title)));
		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_data_name_edit_text);

		solo.enterText(editText, userVariableName);
		finishUserVariableCreationSafeButSlow(userVariableName, true);

		solo.goBack();
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		assertTrue("Variable not set in spinner after creation", solo.searchText(userVariableName));
		Spinner userVariableSpinner = (Spinner) UiTestUtils.getViewContainerByIds(solo, R.id.change_variable_spinner,
				R.id.formula_editor_brick_space);
		assertEquals("UserVariable count not as expected in spinner", 2, userVariableSpinner.getAdapter().getCount());
		assertEquals("Variable not set in spinner after creation", 1, userVariableSpinner.getSelectedItemPosition());

		solo.goBack();
		assertTrue("ScriptFragment not visible", solo.waitForFragmentByTag(ScriptFragment.TAG));
		assertTrue("Variable not set in spinner after deletion", solo.searchText(userVariableName));
		userVariableSpinner = (Spinner) solo.getView(R.id.change_variable_spinner);
		assertEquals("UserVariable count not as expected in spinner", 2, userVariableSpinner.getAdapter().getCount());
		UserVariable userVariable = (UserVariable) Reflection.getPrivateField(UserVariableBrick.class, changeVariableBrick, "userVariable");
		assertNotNull("UserVariable is null", userVariable);
		assertTrue("UserVariable Name not as expected", userVariable.getName().equals(userVariableName));
	}

	public void testViewInFormulaEditorAfterClone() {

		String userVariableName = "testVariable1";
		String userVariableNameTwo = "testVariable2";

		solo.clickOnView(solo.getView(R.id.brick_change_variable_edit_text));
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("Data Fragment not shown", solo.waitForFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG));

		solo.clickOnView(solo.getView(R.id.button_add));
		assertTrue("Add Data Dialog not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_data_dialog_title)));
		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_data_name_edit_text);

		solo.enterText(editText, userVariableName);
		finishUserVariableCreationSafeButSlow(userVariableName, true);

		solo.goBack();
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));

		solo.clickOnText(solo.getString(R.string.brick_change_variable));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_copy_brick));

		solo.clickOnText(solo.getString(R.string.brick_change_variable));

		solo.clickOnText(userVariableName);

		solo.clickOnText(solo.getString(R.string.brick_variable_spinner_create_new_variable));

		EditText editTextTwo = (EditText) solo.getView(R.id.dialog_formula_editor_data_name_edit_text);

		solo.enterText(editTextTwo, userVariableNameTwo);
		solo.clickOnButton(solo.getString(R.string.ok));

		solo.clickOnText(solo.getString(R.string.brick_change_variable));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_move_brick));

		ArrayList<Integer> yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		int addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);
		solo.drag(20, 20, addedYPosition, yPosition.get(yPosition.size() - 1) + 20, 20);

		solo.clickOnText(solo.getString(R.string.brick_change_variable));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_formula_edit_brick));

		assertTrue("UserVariable in view is not right displayed , maybe clone() is broken...",
				solo.searchText(userVariableName, true));

		solo.sleep(2000);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new SingleSprite("cat");
		Script script = new StartScript();
		changeVariableBrick = new ChangeVariableBrick(10);
		script.addBrick(changeVariableBrick);

		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	private void finishUserVariableCreationSafeButSlow(String itemString, boolean forAllSprites) {
		int iteration = 0;

		solo.clickOnButton(solo.getString(R.string.ok));
		solo.waitForText(solo.getString(R.string.formula_editor_data), 0, 1000);

		while (!solo.searchText(solo.getString(R.string.formula_editor_data), true)) {

			if (iteration++ < MAX_ITERATIONS && iteration > 1) {
				solo.goBack();
				assertTrue("Data Fragment not shown",
						solo.waitForText(solo.getString(R.string.formula_editor_variables), 0, 4000));
				solo.clickOnView(solo.getView(R.id.button_add));
				assertTrue("Add Data Dialog not shown",
						solo.waitForText(solo.getString(R.string.formula_editor_variable_dialog_title)));

				EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_data_name_edit_text);
				solo.enterText(editText, itemString);

				if (forAllSprites) {
					assertTrue("Variable Dialog not shown",
							solo.waitForText(solo.getString(R.string.formula_editor_dialog_for_all_sprites)));
					solo.clickOnText(solo.getString(R.string.formula_editor_dialog_for_all_sprites));
				} else {
					assertTrue("Variable Dialog not shown", solo.waitForText(solo
							.getString(R.string.formula_editor_dialog_for_this_sprite_only)));
					solo.clickOnText(solo.getString(R.string.formula_editor_dialog_for_this_sprite_only));
				}
			}
			Log.i(TAG, "(" + iteration + ")OkButton-found: " + solo.searchButton(solo.getString(R.string.ok)));

			solo.clickOnButton(solo.getString(R.string.ok));
			solo.waitForText(solo.getString(R.string.formula_editor_data), 0, 1000);
		}
	}
}
