package com.seu.ums.demo.ui;

import com.seu.ums.demo.eception.ResourseNotFoundException;
import com.seu.ums.demo.model.LoginToken;
import com.seu.ums.demo.service.LoginTokenService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Route("")
public class LogInView extends Dialog implements Serializable {

    private LoginTokenService loginTokenService;
    private HttpSession httpSession;
    private LoginToken loginToken;
    private TextField usernamefield;
    private PasswordField passwordField;
    private Button loginbutton;


    public LogInView(LoginTokenService loginTokenService, HttpSession httpSession) {
        super();
        this.loginTokenService= loginTokenService;
        this.httpSession = httpSession;
        Image logo = new Image();
        loginbutton = new Button("Login");
       // logo.setHeight("100px");
        loginbutton.setIcon(VaadinIcon.SIGN_IN.create());
        loginbutton.setThemeName("primary");
        logo.setSrc("https://upload.wikimedia.org/wikipedia/commons/f/fe/XU_exponential_university.png");
        Label massage = new Label();
        massage.getStyle().set("color","red");

        usernamefield = new TextField("","Id/initial");
        passwordField= new PasswordField("","password");

        loginbutton.addClickListener(buttonClickEvent -> {
            if(usernamefield.isEmpty()||passwordField.isEmpty()){
              Notification.show("Enter username and password");
            }else {
                loginToken = new LoginToken();
                try {
                    loginToken = loginTokenService.authentication(usernamefield.getValue(), passwordField.getValue());
                     httpSession.setAttribute("user", loginToken);
                    switch (loginToken.getRole()) {
                        case NO_ROLE:
                            break;
                        case ADMISSION_OFFICER:
                            httpSession.setAttribute("user", loginToken);
                            loginbutton.getUI().ifPresent(ui -> ui.navigate("admission"));
                            break;
                        case COORDINATOR:
                            httpSession.setAttribute("user",loginToken);
                            System.out.println(loginToken.toString());
                            loginbutton.getUI().ifPresent(ui -> ui.navigate("coordinator"));
                            break;
                        case STUDENT:
                            httpSession.setAttribute("user",loginToken);
                            loginbutton.getUI().ifPresent(ui -> ui.navigate("student"));
                            break;
                        case Faculty:
                            httpSession.setAttribute("user",loginToken);
                            loginbutton.getUI().ifPresent(ui -> ui.navigate("faculty"));
                            break;
                        case DEPUTY_REGISTRAR_Academic:
                            httpSession.setAttribute("user",loginToken);
                            loginbutton.getUI().ifPresent(ui -> ui.navigate("register"));
                            break;
                        case DEPUTY_REGISTRAR_HRM:
                            httpSession.setAttribute("user",loginToken);
                            loginbutton.getUI().ifPresent(ui -> ui.navigate("hrm"));
                            break;
                    }

                } catch (ResourseNotFoundException e) {
                    massage.setText("Incorrect username/password");

                } catch (Exception e) {
                    massage.setText("Incorrect username/password");
                }
            }
                });

        usernamefield.addFocusListener(event -> massage.setText(""));
        passwordField.addFocusListener(event -> massage.setText(""));

        FormLayout formLayout = new FormLayout();


         formLayout.add(logo,usernamefield,passwordField,massage);
         formLayout.add(loginbutton);
         add(formLayout);
         setWidth("300px");

        setCloseOnOutsideClick(false);
        open();

    }
}
