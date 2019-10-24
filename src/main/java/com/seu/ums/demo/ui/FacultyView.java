package com.seu.ums.demo.ui;

import com.seu.ums.demo.model.LoginToken;
import com.seu.ums.demo.model.Role;
import com.seu.ums.demo.model.Scetion;
import com.seu.ums.demo.model.StudentInfo;
import com.seu.ums.demo.service.SectionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
@Route("faculty")
public class FacultyView  extends AppLayout {
    private Map<Tab , Component> containerView;
    private Tabs tabs;
    private Grid<Scetion> sectionGrid;
    private Dialog studentViewDialog;
    private SectionService sectionService;
    private HttpSession httpSession;
    private Grid<Scetion> sectionGridStudents;
    private VerticalLayout verticalLayout;
    private Grid<StudentInfo> studentInfoGrid;

    public FacultyView(SectionService sectionService, HttpSession httpSession) {
        this.sectionService = sectionService;
        this.httpSession = httpSession;
        initialize();
          Header header =new Header(httpSession);
        LoginToken loginToken = header.getLoginToken();
        header.addAttachListener(event -> {
            try {
                if (!loginToken.getRole().equals(Role.Faculty)) {
                    httpSession.removeAttribute("user");
                    header.getUI().ifPresent(ui -> ui.navigate(""));
                }
            }catch (NullPointerException e){
                httpSession.removeAttribute("user");
                header.getUI().ifPresent(ui -> ui.navigate(""));
            }
        });

          addToNavbar(new DrawerToggle());
          tabs= new Tabs(dashborad(),user());
          tabs.setSelectedIndex(0);
         setContent(containerView.get(tabs.getSelectedTab()));
         tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addSelectedChangeListener(selectedChangeEvent -> {
            Tab  selectedTab = selectedChangeEvent.getSelectedTab();
            Component component = containerView.get(selectedTab);
            setContent(component);
        });
        addToDrawer(tabs);

        setContent(containerView.get(tabs.getSelectedTab()));
        addToNavbar(header);

        sectionGrid.setItems(sectionService.findByFaculty(loginToken.getUserId()));
        setSectionGrid();
    }

    private Tab user() {
        Span span = new Span("Profile");
        Tab tab = new Tab(new HorizontalLayout(span));
        containerView.put(tab,  userView());
        return tab;
    }

    private  VerticalLayout userView() {
        VerticalLayout  userLayout = new VerticalLayout();
        H3 h3 = new H3("Profile");
        userLayout.add(h3);
        return userLayout;
    }



    private  Tab dashborad() {
        Span label = new Span("Dashboard");
        Tab tab = new Tab(new HorizontalLayout(label));
        containerView.put(tab,dashboardView());
        return tab;
    }
    private  VerticalLayout dashboardView() {
        VerticalLayout  dashboardLayout = new VerticalLayout();
        H2 h2 = new H2(" Section List:-");
        dashboardLayout.add(h2);

        dashboardLayout.add(sectionGrid);
        return  dashboardLayout;

    }
    public  void setSectionGrid(){
        sectionGrid
                .addColumn(Scetion::getSectionID)
                .setHeader("Section Id");
        sectionGrid
                .addColumn(scetion -> scetion.getCourse().getCode())
                .setHeader("Course Code");
        sectionGrid
                .addColumn( scetion -> scetion.getCourse().getTitle())
                .setFlexGrow(2)
                .setHeader("Course Title");
        sectionGrid
                .addColumn(Scetion::getSection_number)
                .setHeader("Section number");
        sectionGrid
                .addColumn(Scetion::getFaculty)
                .setHeader("Faculty");
        sectionGrid
                .addColumn(Scetion::getCapacity)
                .setHeader("Limit");
        sectionGrid
                .addColumn(scetion -> scetion.getStudentInfos().size())
                .setHeader("Advised");
        sectionGrid
                .addColumn(Scetion::getSemester_id)
                .setHeader("Semester id");
        sectionGrid.addItemClickListener(scetionItemClickEvent -> {
            studentViewDialog.open();
            studentInfoGrid.setItems(scetionItemClickEvent.getItem().getStudentInfos());
            setStudentViewDialog();
        });
    }
    private Button cancleButton = new Button("Cancel", VaadinIcon.CLOSE.create(),buttonClickEvent -> {
           studentInfoGrid.removeAllColumns();
           studentViewDialog.close();

    });
  private  void setStudentViewDialog(){
        studentViewDialog.setCloseOnEsc(false);
        studentViewDialog.setCloseOnOutsideClick(false);
        studentInfoGrid
                .addColumn(StudentInfo::getId)
                .setHeader("Student Id");
        studentInfoGrid
                .addColumn(StudentInfo::getName)
                .setHeader("Name");
        studentViewDialog.add(studentInfoGrid,cancleButton);
        studentViewDialog.setWidth("500px");

  }

    private void initialize() {
        containerView = new HashMap<>();
        sectionGrid = new Grid<>();
        studentViewDialog =new Dialog();
        verticalLayout = new VerticalLayout();
        studentInfoGrid= new Grid<>();
    }
}
