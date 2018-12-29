package com.kinespherept

import griffon.javafx.JavaFXGriffonApplication
import groovy.util.logging.Slf4j

@Slf4j
class Launcher {


    static String username = ''
    static String password = ''



    static void main(String[] args) throws Exception {
//        log.info "main - about to call CollectCredsApplication"
//
//        Application.launch(CollectCredsApplication, args);
//
//        log.info "main - finished with CollectCredsApplication"
//
//
//        log.info "Launcher.username=${username}"
//        log.info "Launcher.password=${password}"

        JavaFXGriffonApplication.main(args)
    }

//    @Override
//    void start(Stage primaryStage) throws Exception {
//        primaryStage.setTitle("Hello World!");
//        Button btn = new Button();
//        btn.setText("Say 'Hello World'");
//        btn.setOnAction(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent event) {
//                System.out.println("Hello World!");
//            }
//        });
//
//        StackPane root = new StackPane();
//        root.getChildren().add(btn);
//        primaryStage.setScene(new Scene(root, 300, 250));
//        primaryStage.show();
//    }


}





/*
// before any changes...
class Launcher {
    static void main(String[] args) throws Exception {
        log.info "main - args=${args}"
        //JavaFXGriffonApplication.main(args)
    }
}
*/



