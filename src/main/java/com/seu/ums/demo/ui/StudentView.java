package com.seu.ums.demo.ui;

import com.seu.ums.demo.model.LoginToken;
import com.seu.ums.demo.model.Role;
import com.seu.ums.demo.model.Scetion;
import com.seu.ums.demo.model.StudentInfo;
import com.seu.ums.demo.service.CourseService;
import com.seu.ums.demo.service.SectionService;
import com.seu.ums.demo.service.StudentRegservice;
import com.seu.ums.demo.service.StudentService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("student")
public class StudentView extends AppLayout {
    private Map<Tab, Component> containerView;
    private  Tabs tabs;
    private Grid<Scetion> sectionGrid;
    private Grid<Scetion> advisedGrid;
    private CourseService courseService;
    private SectionService sectionService;
    private StudentRegservice studentRegservice;
    private HttpSession httpSession;
    private StudentService studentService;

    public StudentView(CourseService courseService, SectionService sectionService, StudentRegservice studentRegservice, HttpSession httpSession, StudentService studentService) {
        this.courseService = courseService;
        this.sectionService = sectionService;
        this.studentRegservice = studentRegservice;
        this.httpSession = httpSession;
        this.studentService = studentService;
        Header header = new Header(httpSession);
        initialize();
        addToNavbar(new DrawerToggle());
        tabs= new Tabs(dashboard(),user());
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.setSelectedIndex(0);
        setContent(containerView.get(tabs.getSelectedTab()));
        tabs.addSelectedChangeListener(selectedChangeEvent -> {
            Tab selectedTab = selectedChangeEvent.getSelectedTab();
            Component component = containerView.get(selectedTab);
            setContent(component);
        });
        addToDrawer(tabs);
        addToNavbar(header);
      LoginToken  loginToken = header.getLoginToken();
        header.addAttachListener(event -> {
            try {
                if (!loginToken.getRole().equals(Role.STUDENT)) {
                    httpSession.removeAttribute("user");
                    header.getUI().ifPresent(ui -> ui.navigate(""));
                }
            }catch (NullPointerException e){
                httpSession.removeAttribute("user");
                header.getUI().ifPresent(ui -> ui.navigate(""));
            }
        });


       scetionList= sectionService.findByProgram(programName);
        sectionGrid.setItems(scetionList);
        setSectionGrid();
        userId = loginToken.getUserId();
        userName = loginToken.getUserName();
        try {
            advisedGrid.setItems(studentRegservice.getStudentSections(userId));
        }catch (Exception e){
            System.out.println(e);
        }

        setAdvisedGrid();
      try {
          checkCredit();
      }catch (Exception e){
          System.out.println(e);
      }


    }

    private void initialize(){
        sectionGrid = new Grid<>();
        advisedGrid = new Grid<>();
        containerView = new HashMap<>();
        tabs = new Tabs();

    }
    private  String userId;
    private  String userName;
    private String  programName= "Bsc in CSE";
    private List<Scetion> scetionList;

    private  Tab user() {
        Span span = new Span("Profile");
        Tab tab = new Tab(new HorizontalLayout(span));
        containerView.put(tab,userView());
        return tab;
    }

    private VerticalLayout userView() {
        VerticalLayout userLayout = new VerticalLayout();
        H3 h3 = new H3("Profile->");
        userLayout.add(h3);
        return userLayout;
    }

    private  Tab dashboard() {
        Span  label = new Span("Advising");
        Tab tab = new Tab(new HorizontalLayout(label));
        containerView.put(tab, dashboardView());
        return tab;
    }
    private H4 h4creditStatus = new H4();
    private  VerticalLayout dashboardView() {
       VerticalLayout verticalLayout = new VerticalLayout();
       H3 h3 = new H3("Advised course");
       verticalLayout.add(h3,h4creditStatus,advisedGrid,h4creditStatus);
       //advisedGrid.setWidth("650px");
       H3 label = new H3("Section list");
       verticalLayout.add(label,sectionGrid);
      // setSectionGrid();
       return verticalLayout;

    }
    private void setSectionGrid(){
        sectionGrid.addColumn(scetion -> scetionList.indexOf(scetion)+1)
                   .setFlexGrow(0);
        sectionGrid
                .addColumn(scetion -> scetion.getCourse().getCode())
                .setHeader("Course code");
        sectionGrid
                .addColumn(scetion -> scetion.getCourse().getTitle())
                .setFlexGrow(1)
                .setHeader("Course title");
        sectionGrid
                .addColumn(scetion -> scetion.getCourse().getCredit())
                .setFlexGrow(0)
                .setHeader("Credit");
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


         sectionGrid.addItemClickListener(scetionItemClickEvent -> {
             StudentInfo studentInfo = new StudentInfo(userId,userName);
              //String  sectionId= scetionItemClickEvent.getItem().getSectionID();
              int temp =scetionItemClickEvent.getItem().getCourse().getCredit();
              String tempCode =scetionItemClickEvent.getItem().getCourse().getCode();
             System.out.println("tempSecelctCredit="+temp);
             if (checkCredit()+temp<=9){
                 if (!checkDuplicateCourse(tempCode)){
                     try {
                         studentRegservice.regforScetion(scetionItemClickEvent.getItem().getSectionID(),studentInfo);
                         advisedGrid.removeAllColumns();
                         advisedGrid.setItems(studentRegservice.getStudentSections(userId));
                         setAdvisedGrid();
                         sectionGrid.removeAllColumns();
                         sectionGrid.setItems(sectionService.findByProgram(programName));
                         setSectionGrid();
                         checkCredit();
                         Notification.show("Saved successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                     }catch (HttpClientErrorException.NotAcceptable e){
                         Notification.show("Section is full").addThemeVariants(NotificationVariant.LUMO_ERROR);
                     }
                     catch (Exception e){
                         Notification.show(e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
                         System.out.println(e);
                     }
                 } else {
                     Notification.show("Course Already Exits").addThemeVariants(NotificationVariant.LUMO_ERROR);
                 }
             }else {
                 Notification.show("Not Allow More Then 8 Credit").addThemeVariants(NotificationVariant.LUMO_ERROR);
             }
         });
    }
    private void setAdvisedGrid(){
        advisedGrid
                .addColumn(scetion -> scetion.getCourse().getCode())

                .setHeader("Course Code");
        advisedGrid
                .addColumn(scetion -> scetion.getCourse().getTitle())
                .setHeader("Course title");
        advisedGrid.addColumn(scetion -> scetion.getCourse().getCredit())
                .setFlexGrow(0)
                 .setHeader("Credit");
        advisedGrid
                .addColumn(Scetion::getSection_number)

                .setHeader("Section number");
        advisedGrid
                 .addColumn(Scetion::getFaculty)
                 .setHeader("Faculty");
        advisedGrid.addComponentColumn(this::getDropButton);



    }

    private Component getDropButton(Scetion scetion){
        Button sectionDropButton = new Button("Drop", VaadinIcon.CLOSE_SMALL.create());
          sectionDropButton.addClickListener(buttonClickEvent -> {
              try {
                  String s = studentRegservice.dropScetion(scetion.getSectionID(),userId);
                  advisedGrid.setItems(studentRegservice.getStudentSections(userId));
                  sectionGrid.setItems(sectionService.findByProgram(programName));
                  checkCredit();
                  Notification.show(s).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
              }catch (Exception e){
                  Notification.show(e.getMessage());
                  e.printStackTrace();
              }

          });
        return sectionDropButton;
    }

    private   int  checkCredit(){
         int totalCredit = 0;
         try {

             totalCredit=   studentRegservice.getStudentSections(userId).stream()
                     .mapToInt(scetion -> scetion.getCourse().getCredit())
                     .sum();
         } catch (NullPointerException e){
             return 0;
         }

        System.out.println("TotalCredit="+totalCredit);
        h4creditStatus.setText("Total credit taken : "+totalCredit);
        return totalCredit;

    }
    private boolean checkDuplicateCourse(String code){
        boolean  find;
        try {
          find =  studentRegservice.getStudentSections(userId).stream()
                    .anyMatch(scetion -> scetion.getCourse().getCode().equals(code));
        }catch (NullPointerException e){
            return false;
        }

       return find;

    }


}
