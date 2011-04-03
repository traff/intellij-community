/*
 * Copyright 2000-2011 JetBrains s.r.o.
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
package com.intellij.openapi.diff.impl.dir;

import com.intellij.ui.table.JBTable;
import com.intellij.util.IconUtil;
import com.intellij.util.Icons;
import com.intellij.util.containers.HashMap;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

/**
 * @author Konstantin Bulenkov
 */
public class DirDiffTableCellRenderer extends DefaultTableCellRenderer {
  private final HashMap<String, BufferedImage> cache = new HashMap<String, BufferedImage>();
  private final JBTable myTable;

  public DirDiffTableCellRenderer(JBTable table) {
    myTable = table;
    myTable.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        cache.clear();
      }
    });
  }

  @Override
  public Component getTableCellRendererComponent(final JTable table, Object value, boolean isSelected, boolean hasFocus, final int row, final int column) {
    final DirDiffTableModel model = (DirDiffTableModel)table.getModel();
    final DirDiffElement element = model.myElements.get(row);
    if (element.isSeparator()) {
      return new JPanel() {
        @Override
        public void paint(Graphics g) {
          super.paint(g);
          int offset = 0;
          int i = 0;
          final TableColumnModel columnModel = table.getColumnModel();
          while (i < column) {
            offset += columnModel.getColumn(i).getWidth();
            i++;
          }
          int width = columnModel.getColumn(column).getWidth();
          int height = table.getRowHeight(row);
          final BufferedImage image = getOrCreate(element.getName());
          g.drawImage(image, 0, 0, width, height, offset, 0, offset + width, height, null);
        }
      };
    }

    final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (c instanceof JLabel && (hasFocus || isSelected)) {
      ((JLabel)c).setBorder(noFocusBorder);
    }
    return c;
  }

  private BufferedImage getOrCreate(String path) {
    final BufferedImage image = cache.get(path);
    if (image != null) {
      return image;
    }
    final int w = myTable.getWidth();
    final int h = myTable.getRowHeight();
    final BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    final Icon icon = Icons.FOLDER_ICON;
    final Graphics g = img.getGraphics();
    g.drawImage(IconUtil.toImage(icon), 2, (h - icon.getIconHeight()) / 2, null);
    g.setColor(Color.BLACK);
    g.drawString(path, 2 + icon.getIconWidth() + 2, h - 2);
    cache.put(path, img);
    return img;
  }
}
