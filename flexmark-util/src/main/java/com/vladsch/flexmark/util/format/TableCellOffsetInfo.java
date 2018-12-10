package com.vladsch.flexmark.util.format;

import java.util.List;

import static com.vladsch.flexmark.util.Utils.maxLimit;
import static com.vladsch.flexmark.util.Utils.minLimit;

public class TableCellOffsetInfo {
    final public MarkdownTable table;
    final public int offset;
    final public TableSection section;
    final public TableRow tableRow;        // at or inside cell
    final public TableCell tableCell;        // at or inside cell
    final public int row;               // all rows with separator index
    final public int column;            // at column right before or right after
    final public Integer insideColumn;   // inside column or null
    final public Integer insideOffset;    // offset from start of column or null if not inside column

    public TableCellOffsetInfo(final int offset, final MarkdownTable table, final TableSection section, final TableRow tableRow, final TableCell tableCell, final int row, final int column, final Integer insideColumn, final Integer insideOffset) {
        this.offset = offset;
        this.table = table;
        this.section = section;
        this.tableRow = tableRow;
        this.tableCell = tableCell;
        this.row = row;
        this.column = column;
        this.insideColumn = insideColumn;
        this.insideOffset = insideOffset;
    }

    public boolean isInsideColumn() {
        return insideColumn != null;
    }

    public boolean isBeforeCells() {
        return tableRow != null && tableCell != null && insideColumn == null && offset < tableCell.getStartOffset();
    }

    public boolean isInCellSpan() {
        return tableRow != null && tableCell != null && insideColumn == null && offset >= tableCell.getStartOffset() && offset < tableCell.getEndOffset();
    }

    public boolean isAfterCells() {
        return tableRow != null && tableCell != null && insideColumn == null && offset >= tableCell.getEndOffset();
    }

    public boolean canDeleteColumn() {
        return insideColumn != null && table.getMinColumnsWithoutColumn(column, true) > 0;
    }

    public boolean canDeleteRow() {
        return tableRow != null && section != table.separator && table.body.rows.size() + table.heading.rows.size() > 1;
    }

    public boolean isFirstCell() {
        return isInsideColumn() && column == 0;
    }

    public boolean isLastCell() {
        return isInsideColumn() && column + 1 == tableRow.cells.size();
    }

    public boolean isLastRow() {
        return row + 1 == table.getAllRowsCount();
    }

    /**
     * Only available if inside are set and not in first cell of first row
     *
     * @param insideOffset offset inside the cell, null if same as th
     * @return offset in previous cell or null
     */
    public TableCellOffsetInfo offsetPreviousCell(Integer insideOffset) {
        if (isInsideColumn() && column > 0) {
            TableCell cell = tableRow.cells.get(column - 1);
            if (insideOffset == null) cell.textToInsideOffset(tableCell.insideToTextOffset(this.insideOffset == null ? 0 : this.insideOffset));
            return table.getCellOffsetInfo(cell.getTextStartOffset() + (maxLimit(cell.getCellSize(), minLimit(0, insideOffset))));
        }
        return null;
    }

    /**
     * Only available if tableRow/tableCell are set and not in first cell of first row
     *
     * @param insideOffset offset inside the cell, null if same as th
     * @return offset in previous cell or null
     */
    public TableCellOffsetInfo offsetNextCell(Integer insideOffset) {
        if (isInsideColumn() && column + 1 < tableRow.cells.size()) {
            TableCell cell = tableRow.cells.get(column + 1);
            if (insideOffset == null) cell.textToInsideOffset(tableCell.insideToTextOffset(this.insideOffset == null ? 0 : this.insideOffset));
            return table.getCellOffsetInfo(cell.getTextStartOffset() + (maxLimit(cell.getCellSize(), minLimit(0, insideOffset))));
        }
        return null;
    }

    /**
     * Only available if not at row 0
     *
     * @param insideOffset offset inside the cell, null if same as th
     * @return offset in previous cell or null
     */
    public TableCellOffsetInfo offsetPreviousRow(Integer insideOffset) {
        if (row > 0) {
            List<TableRow> allRows = table.getAllRows();
            TableRow otherRow = allRows.get(this.row - 1);
            if (isInsideColumn() && column < otherRow.cells.size()) {
                // transfer inside offset
                TableCell cell = otherRow.cells.get(column);
                if (insideOffset == null) cell.textToInsideOffset(tableCell.insideToTextOffset(this.insideOffset == null ? 0 : this.insideOffset));
                return table.getCellOffsetInfo(cell.getTextStartOffset() + (maxLimit(cell.getCellSize(), minLimit(0, insideOffset))));
            } else {
                if (isBeforeCells()) {
                    return table.getCellOffsetInfo(otherRow.cells.get(0).getStartOffset());
                } else {
                    return table.getCellOffsetInfo(otherRow.cells.get(otherRow.cells.size() - 1).getEndOffset());
                }
            }
        }
        return null;
    }

    /**
     * Only available if not at last row
     *
     * @param insideOffset offset inside the cell, null if same as th
     * @return offset in previous cell or null
     */
    public TableCellOffsetInfo offsetNextRow(Integer insideOffset) {
        if (row + 1 < table.getAllRowsCount()) {
            List<TableRow> allRows = table.getAllRows();
            TableRow otherRow = allRows.get(this.row + 1);
            if (isInsideColumn() && column < otherRow.cells.size()) {
                // transfer inside offset
                TableCell cell = otherRow.cells.get(column);
                if (insideOffset == null) cell.textToInsideOffset(tableCell.insideToTextOffset(this.insideOffset == null ? 0 : this.insideOffset));
                return table.getCellOffsetInfo(cell.getTextStartOffset() + (maxLimit(cell.getCellSize(), minLimit(0, insideOffset))));
            } else {
                if (isBeforeCells()) {
                    return table.getCellOffsetInfo(otherRow.cells.get(0).getStartOffset());
                } else {
                    return table.getCellOffsetInfo(otherRow.cells.get(otherRow.cells.size() - 1).getEndOffset());
                }
            }
        }
        return null;
    }

    /**
     * Available if somewhere in table
     *
     * @return offset in previous cell or null
     */
    public TableCellOffsetInfo offsetNextTab() {
        if (offset <= table.getTableStartOffset()) {
            return table.getCellOffsetInfo(table.getAllRows().get(0).cells.get(0).getTextStartOffset());
        } else if (isBeforeCells()) {
            // go to first cell of row
            return table.getCellOffsetInfo(tableCell.getTextStartOffset());
        } else if (isAfterCells() || isLastCell()) {
            // go to first cell of next row or caption if we have it, or right after the table
            if (row + 1 < table.getAllRowsCount()) {
                List<TableRow> allRows = table.getAllRows();
                return table.getCellOffsetInfo(allRows.get(row + 1).cells.get(0).getTextStartOffset());
            } else if (!table.getCaptionOpen().isEmpty()) {
                if (!table.getCaption().isEmpty()) {
                    return table.getCellOffsetInfo(table.getCaption().getStartOffset());
                } else {
                    return table.getCellOffsetInfo(table.getCaptionOpen().getEndOffset());
                }
            } else {
                List<TableRow> allRows = table.getAllRows();
                TableRow lastRow = allRows.get(allRows.size() - 1);
                TableCell lastCell = lastRow.cells.get(lastRow.cells.size() - 1);
                int offset = lastCell.getEndOffset();
                int eolPos = lastCell.text.endOfLineAnyEOL(offset);
                return table.getCellOffsetInfo(offset + (eolPos == -1 ? 0 : eolPos + lastCell.text.eolLength(eolPos)));
            }
        } else {
            // go to next cell of this row
            return table.getCellOffsetInfo(tableRow.cells.get(column+1).getTextStartOffset());
        }
    }

    /**
     * Available if somewhere in table
     *
     * @return offset in previous cell or null
     */
    public TableCellOffsetInfo offsetPreviousTab() {
        if (!table.getCaptionClose().isEmpty() && offset >= table.getCaptionClose().getStartOffset()
                || !table.getCaption().isEmpty() && offset > table.getCaption().getEndOffset()) {
            return table.getCellOffsetInfo(table.getCaption().isEmpty() ? table.getCaptionOpen().getEndOffset() : table.getCaption().getStartOffset());
        } else if (row == 0 && (isFirstCell() || isBeforeCells())) {
            return table.getCellOffsetInfo(table.getTableStartOffset());
        } else if (isAfterCells()) {
            // go to last cell of this row or previous row
            int toRow = row < table.getAllRowsCount() ? row : table.getAllRowsCount() - 1;
            List<TableRow> allRows = table.getAllRows();
            return table.getCellOffsetInfo(allRows.get(row + 1).cells.get(0).getTextStartOffset());
        } else if (isBeforeCells() || isFirstCell()) {
            // go to last cell of previous row
            List<TableRow> allRows = table.getAllRows();
            TableRow otherRow = allRows.get(this.row - 1);
            return table.getCellOffsetInfo(otherRow.cells.get(otherRow.cells.size() - 1).getTextStartOffset());
        } else {
            // go to previous cell
            return table.getCellOffsetInfo(tableRow.cells.get(column-1).getTextStartOffset());
        }
    }

    @Override
    public String toString() {
        return "CellOffsetInfo{" +
                " offset=" + offset +
                ", row=" + row +
                ", column=" + column +
                ", insideColumn=" + insideColumn +
                ", insideOffset=" + insideOffset +
                '}';
    }
}