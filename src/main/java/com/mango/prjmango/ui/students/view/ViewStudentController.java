package com.mango.prjmango.ui.students.view;

import com.mango.prjmango.Main;
import com.mango.prjmango.student.Student;
import com.mango.prjmango.student.StudentSortTypes;
import com.mango.prjmango.ui.common.ImageIcons;
import com.mango.prjmango.ui.dialogs.confirmation.ConfirmationController;
import com.mango.prjmango.ui.dialogs.confirmation.ConfirmationView;
import com.mango.prjmango.ui.dialogs.confirmation.Dialogs;
import com.mango.prjmango.ui.students.StudentsView;
import com.mango.prjmango.ui.students.view.reports.ReportsController;
import com.mango.prjmango.ui.students.view.reports.ReportsView;
import com.mango.prjmango.utilities.dbcommands.StudentCommands;
import lombok.SneakyThrows;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ViewStudentController {

    public ViewStudentController(ViewStudentView view) {
        populateTable(view, Main.getStudents().getStudents());

        JTextField searchTextField = view.getSearchTextBox();
        searchTextField.getDocument().addDocumentListener(new SearchTextBoxKeyListener(view, view.getModel()));

        JTableHeader header = view.getStudentTable().getTableHeader();
        header.addMouseListener(new StudentsTableMouseListener(view, view.getStudentTable(), view.getModel(), header));
    }

    private static void populateTable(ViewStudentView view, List<Student> students) {
        for (Student student : students) {
            view.getModel().addRow(new Object[]{
                    student.getStudentID(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getGrade(),
                    student.getBio()});
        }
        view.getStudentTable().getColumn("View").setCellRenderer(new ViewButtonRenderer());
        view.getStudentTable().getColumn("View").setCellEditor(new ViewButtonEditor(view, new JCheckBox()));

        view.getStudentTable().getColumn("Delete").setCellRenderer(new DeleteButtonRenderer());
        view.getStudentTable().getColumn("Delete").setCellEditor(new DeleteButtonEditor(view, new JCheckBox()));
    }

    private static class ViewButtonRenderer extends DefaultTableCellRenderer {
        JLabel label = new JLabel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            label = new JLabel(ImageIcons.PASSWORD_OPEN_EYE_NO_HOVER.getImageIcon());
            return label;
        }
    }

    private static class ViewButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private final ViewStudentView view;

        public ViewButtonEditor(ViewStudentView view, JCheckBox checkBox) {
            super(checkBox);
            this.view = view;
            button = new JButton();
            button.setOpaque(false);
            button.setBackground(null);
            button.setBorder(null);
            button.setContentAreaFilled(false);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if(isSelected){
//                System.out.println("Selected");

            }else{
//                System.out.println("Not Selected");
            }
            isPushed = true;
            return button;
        }

        @SneakyThrows
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                System.out.println(view.getModel().getValueAt(view.getStudentTable().getSelectedRow(),0) +" was Clicked");
                String fullName = view.getModel().getValueAt(view.getStudentTable().getSelectedRow(),2) +", "+ view.getModel().getValueAt(view.getStudentTable().getSelectedRow(),1);
                ReportsView reportsView = new ReportsView(fullName);
                new ReportsController(reportsView, (Integer) view.getModel().getValueAt(view.getStudentTable().getSelectedRow(),0));
                StudentsView.setActiveDisplay(reportsView);
           }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private static class DeleteButtonRenderer extends DefaultTableCellRenderer {
        JLabel label = new JLabel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            label = new JLabel(ImageIcons.DELETE_TRASH.getImageIcon());
            return label;
        }
    }

    private static class DeleteButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private final ViewStudentView view;

        public DeleteButtonEditor(ViewStudentView view, JCheckBox checkBox) {
            super(checkBox);
            this.view = view;
            button = new JButton();
            button.setOpaque(false);
            button.setBackground(null);
            button.setBorder(null);
            button.setContentAreaFilled(false);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if(isSelected){
//                System.out.println("Selected");

            }else{
//                System.out.println("Not Selected");
            }
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                System.out.println(view.getModel().getValueAt(view.getStudentTable().getSelectedRow(),0) +" was Clicked");
                int student_id = (Integer) view.getModel().getValueAt(view.getStudentTable().getSelectedRow(),0);
                String firstName = (String) view.getModel().getValueAt(view.getStudentTable().getSelectedRow(),1);
                int totalAssignments = StudentCommands.getTotalAssignmentsFromStudent(student_id);

                 ConfirmationView confirmationView =
                         new ConfirmationView(firstName + ": " + totalAssignments + " assignments. Are you sure?", Dialogs.DELETE_STUDENT);
                 new ConfirmationController(confirmationView,student_id,0);
                 confirmationView.requestFocusInWindow();

            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private static class SearchTextBoxKeyListener implements DocumentListener {

        private final ViewStudentView view;
        private final DefaultTableModel model;

        public SearchTextBoxKeyListener(ViewStudentView view, DefaultTableModel model) {
            this.view  = view;
            this.model = model;
        }

        /**
         * Gives notification that there was an insert into the document.  The
         * range given by the DocumentEvent bounds the freshly inserted region.
         *
         * @param e the document event
         */
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateTable();
        }


        /**
         * Gives notification that a portion of the document has been
         * removed.  The range is given in terms of what the view last
         * saw (that is, before updating sticky positions).
         *
         * @param e the document event
         */
        @Override
        public void removeUpdate(DocumentEvent e) {
            updateTable();
        }

        /**
         * Gives notification that an attribute or set of attributes changed.
         *
         * @param e the document event
         */
        @Override
        public void changedUpdate(DocumentEvent e) {
            updateTable();
        }

        private void updateTable() {
            String currText = view.getSearchTextBox().getText().toLowerCase(Locale.ROOT);

            if (currText.length() == 0) {
                populateTable(view, Main.getStudents().getStudents());
            }

            List<Student> newList = new ArrayList<>();
            for (Student stud : Main.getStudents().getStudents()) {
                if (stud.getFirstName().toLowerCase(Locale.ROOT).startsWith(currText)) {
                    newList.add(stud);
                }
            }

            clearTable(model);

            populateTable(view, newList);
        }
    }

    private static void clearTable(DefaultTableModel model) {
        model.setRowCount(0);
    }

    private static class StudentsTableMouseListener implements MouseListener {

        private final ViewStudentView view;
        private final JTable table;
        private final DefaultTableModel model;
        private final JTableHeader header;

        /**
         * Constructor. Initializes instance variables that will be used throughout the {@link MouseListener}
         * methods.
         *
         * @param view   the {@link ViewStudentView} instance
         * @param table  the {@link JTable}
         * @param model  the {@link DefaultTableModel} of the specific {@link JTable}
         * @param header the {@link JTableHeader} of the specific {@link JTable}
         */
        public StudentsTableMouseListener(
                ViewStudentView view,
                JTable table,
                DefaultTableModel model,
                JTableHeader header) {
            this.view   = view;
            this.table  = table;
            this.model  = model;
            this.header = header;
        }

        /**
         * Invoked when the mouse button has been clicked (pressed
         * and released) on a component.
         *
         * @param e the {@link MouseEvent}
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = header.columnAtPoint(e.getPoint());
            String name = table.getColumnName(col);

            List<Student> students;
            switch (name) {
                case "First Name":
                    students = Main.getStudents().getStudents();
                    students.sort(StudentSortTypes.FIRST_NAME.getComparator());
                    clearTable(model);
                    populateTable(view, students);
                    break;
                case "Last Name":
                    students = Main.getStudents().getStudents();
                    students.sort(StudentSortTypes.LAST_NAME.getComparator());
                    clearTable(model);
                    populateTable(view, students);
                    break;
                case "Grade":
                    students = Main.getStudents().getStudents();
                    students.sort(StudentSortTypes.GRADE.getComparator());
                    clearTable(model);
                    populateTable(view, students);
                    break;
                case "Bio":
                    students = Main.getStudents().getStudents();
                    students.sort(StudentSortTypes.BIO.getComparator());
                    clearTable(model);
                    populateTable(view, students);
                    break;
                default:
                    break;
            }
        }

        /**
         * Invoked when a mouse button has been pressed on a component.
         *
         * @param e the {@link MouseEvent}
         */
        @Override
        public void mousePressed(MouseEvent e) { /* Not needed */ }

        /**
         * Invoked when a mouse button has been released on a component.
         *
         * @param e the {@link MouseEvent}
         */
        @Override
        public void mouseReleased(MouseEvent e) { /* Not needed */ }

        /**
         * Invoked when the mouse enters a component.
         *
         * @param e the {@link MouseEvent}
         */
        @Override
        public void mouseEntered(MouseEvent e) { /* Not needed */ }

        /**
         * Invoked when the mouse exits a component.
         *
         * @param e the {@link MouseEvent}
         */
        @Override
        public void mouseExited(MouseEvent e) { /* Not needed */ }
    }
}
