/*
 * Copyright 2003-2007 Dave Griffith, Bas Leijdekkers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.siyeh.ig.naming;

import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiVariable;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.InspectionGadgetsFix;
import com.siyeh.ig.fixes.RenameFix;
import com.siyeh.ig.ui.AddAction;
import com.siyeh.ig.ui.IGTable;
import com.siyeh.ig.ui.ListWrappingTableModel;
import com.siyeh.ig.ui.RemoveAction;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.*;

public class QuestionableNameInspection extends BaseInspection {

    /** @noinspection PublicField*/
    @NonNls public String nameString = "foo,bar,baz";

    List<String> nameList = new ArrayList<String>(32);

    public QuestionableNameInspection(){
        parseString(nameString, nameList);
    }

    @NotNull
    public String getDisplayName(){
        return InspectionGadgetsBundle.message(
                "questionable.name.display.name");
    }

    @NotNull
    public String buildErrorString(Object... infos){
        return InspectionGadgetsBundle.message(
                "questionable.name.problem.descriptor");
    }

    public void readSettings(Element element) throws InvalidDataException{
        super.readSettings(element);
        parseString(nameString, nameList);
    }

    public void writeSettings(Element element) throws WriteExternalException{
        nameString = formatString(nameList);
        super.writeSettings(element);
    }

    public JComponent createOptionsPanel(){
        final Form form = new Form();
        return form.getContentPanel();
    }

    protected InspectionGadgetsFix buildFix(PsiElement location){
        return new RenameFix();
    }

    protected boolean buildQuickFixesOnlyForOnTheFlyErrors(){
        return true;
    }

    public BaseInspectionVisitor buildVisitor(){
        return new QuestionableNameVisitor();
    }

    private class QuestionableNameVisitor extends BaseInspectionVisitor{

        private final Set<String> nameSet = new HashSet(nameList);

        public void visitVariable(@NotNull PsiVariable variable){
            final String name = variable.getName();
            if(nameSet.contains(name)){
                registerVariableError(variable);
            }
        }

        public void visitMethod(@NotNull PsiMethod method){
            final String name = method.getName();
            if(nameSet.contains(name)){
                registerMethodError(method);
            }
        }

        public void visitClass(@NotNull PsiClass aClass){
            final String name = aClass.getName();
            if(nameSet.contains(name)){
                registerClassError(aClass);
            }
        }
    }

    private class Form{

        JPanel contentPanel;
        JButton addButton;
        JButton removeButton;
        IGTable table;

        Form(){
            super();
            addButton.setAction(new AddAction(table));
            removeButton.setAction(new RemoveAction(table));
        }

        private void createUIComponents() {
            table = new IGTable(new ListWrappingTableModel(
                    nameList, InspectionGadgetsBundle.message(
                    "questionable.name.column.title")));
        }

        public JComponent getContentPanel(){
            return contentPanel;
        }
    }
}