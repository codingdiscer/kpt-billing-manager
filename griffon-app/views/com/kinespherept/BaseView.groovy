package com.kinespherept

import com.google.common.collect.Lists
import com.kinespherept.autowire.SpringAutowire
import com.kinespherept.config.GriffonConfig
import com.kinespherept.config.SpringConfig
import com.kinespherept.manager.SceneManager
import com.kinespherept.model.navigation.SceneDefinition
import griffon.core.artifact.GriffonController
import griffon.core.artifact.GriffonModel
import griffon.javafx.beans.binding.UIThreadAwareBindings
import groovy.util.logging.Slf4j
import javafx.beans.property.Property
import javafx.beans.property.StringProperty
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Control
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.PasswordField
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Superclass for the Griffon view classes that offers various utilities, such as:
 * * auto-binding of model fields to associated FXML elements within a view
 * * auto-wiring of Spring context loaded beans into fields annotated with {@link SpringAutowire}
 * * auto-wiring of Griffon Application MVC-group declared components into fields annotated with {@link com.kinespherept.autowire.GriffonAutowire}
 */
@Slf4j
abstract class BaseView extends AbstractJavaFXGriffonView {

    /**
     * Declares the types of FXML controls that we know how to auto-bind to appropriate fields
     */
    static List<Class<Control>> BINDABLE_CONTROL_TYPES = [TextField, Label, TextArea, ListView, ChoiceBox, DatePicker, PasswordField ]

    /**
     * We all need access to the {@link SceneManager}
     */
    SceneManager sceneManager

    /**
     * Keeps track of any auto-bound {@link StringProperty} connections (to bind the view with the model) so that
     * they aren't garbage collected
     */
    List<Property> bindings

    /**
     * Holds a reference to the scene that is built for this view
     */
    Scene scene

    /**
     * Performs the auto-wiring of all fields on the given object that are annotated with {@link SpringAutowire} with
     * the appropriate bean from the Spring context
     * @param that The object that needs some auto-wiring help
     */
    void baseInit(Object that) {
        SpringConfig.autowire(that)
        sceneManager = SceneManager.getManager()
    }

    /**
     * Creates the connection between model fields that are annotated with {@link griffon.transform.FXObservable} and
     * their corresponding FXML view controls declared in this view (or sub-classes of).
     * Returns a list of {@link StringProperty} objects that represent the connection.  The returned list must be
     * held in a field (declared as "bindings" in this class), or the bindings would be lost to garbage collection.
     *
     * @param node The parent node to search for eligible bindings
     * @param model The model class with fields to bind to controls in the view
     * @return A list of bound properties
     */
    List<StringProperty> autoBind(Node node, GriffonModel model) {
        // create the list of properties that we'll try to populate, then return
        List<StringProperty> bindings = Lists.newArrayList()

        // search through all the declared fields in the given model class (plus super-classes)
        for(Field field : SpringConfig.getAllDeclaredFields(model.class)) {
            if(field.name.endsWith('ChoiceProp')) {
                log.debug("field :: ${field.name}, type=${field.getType()}")

                String observableName = field.name.replace('ChoiceProp', '')
                log.debug("..will look for UI Node with id : [${observableName}]")

                Node bindableNode = findNode(node, observableName)

                // see if a candidate node was found, and also that the candidate is one of the known bindable class types
                if(bindableNode != null && BINDABLE_CONTROL_TYPES.contains(bindableNode.class) ) {
                    log.debug("..found bindable node! :: bindableNode.class=${bindableNode.class.name};  id=${bindableNode.id}; will dynamically call method ${field.name}erty()")

                    String methodName = "${field.name}erty"
                    Method method = model.class.getMethod(methodName, null)

                    // the property binding that we need to hold on to
                    Property bindableProp


                    if(bindableNode instanceof ChoiceBox) {
                        bindableProp = UIThreadAwareBindings.uiThreadAwareStringProperty(
                                method.invoke(model, null))
                        (bindableNode as ChoiceBox).valueProperty().bindBidirectional(bindableProp)
                    }
                    bindings.add(bindableProp)
                }

            } else if(field.name.endsWith('Prop')) {
                log.debug("field :: ${field.name}, type=${field.getType()}")

                String observableName = field.name.replace('Prop', '')
                log.debug("..will look for UI Node with id : [${observableName}]")

                Node bindableNode = findNode(node, observableName)



                // see if a candidate node was found, and also that the candidate is one of the known bindable class types
                if(bindableNode != null && BINDABLE_CONTROL_TYPES.contains(bindableNode.class) ) {
                    log.debug("..found bindable node! :: bindableNode.class=${bindableNode.class.name};  id=${bindableNode.id}; will dynamically call method ${field.name}erty()")

                    String methodName = "${field.name}erty"
                    Method method = model.class.getMethod(methodName, null)

                    // the property binding that we need to hold on to
                    Property bindableProp

                    // the money shot!!
                    if(bindableNode instanceof TextField) {
                        bindableProp = UIThreadAwareBindings.uiThreadAwareStringProperty(
                                method.invoke(model, null))
                        (bindableNode as TextField).textProperty().bindBidirectional(bindableProp)
                    } else if(bindableNode instanceof TextArea) {
                        bindableProp = UIThreadAwareBindings.uiThreadAwareStringProperty(
                                method.invoke(model, null))
                        (bindableNode as TextArea).textProperty().bindBidirectional(bindableProp)
                    } else if(bindableNode instanceof Label) {
                        bindableProp = UIThreadAwareBindings.uiThreadAwareStringProperty(
                                method.invoke(model, null))
                        (bindableNode as Label).textProperty().bindBidirectional(bindableProp)
                    } else if(bindableNode instanceof ListView) {
                        bindableProp = UIThreadAwareBindings.uiThreadAwareListProperty(
                                method.invoke(model, null))
                        (bindableNode as ListView).itemsProperty().bindBidirectional(bindableProp)
                    } else if(bindableNode instanceof ChoiceBox) {
                        bindableProp = UIThreadAwareBindings.uiThreadAwareListProperty(
                                method.invoke(model, null))
                        (bindableNode as ChoiceBox).itemsProperty().bindBidirectional(bindableProp)
                    } else if(bindableNode instanceof DatePicker) {
                        bindableProp = UIThreadAwareBindings.uiThreadAwareObjectProperty(
                                method.invoke(model, null))
                        (bindableNode as DatePicker).valueProperty().bindBidirectional(bindableProp)
                    } else if(bindableNode instanceof PasswordField) {
                        bindableProp = UIThreadAwareBindings.uiThreadAwareStringProperty(
                                method.invoke(model, null))
                        (bindableNode as PasswordField).textProperty().bindBidirectional(bindableProp)
                    } else {
                        log.debug "..unable to match up field [${field.name}] with an appropriate UI element"
                    }
                    bindings.add(bindableProp)
                }

            }
        }

        // look for methods in the model to attach to read-only properties
        // TODO - figure this out - look through the methods of the Model class
        // TODO... to find Closures that can be attached to ListView.selectionModel.selectedIndex



        // return the bindings that were created
        bindings
    }

    /**
     * Recursively searches the tree of Node objects to find one with the given <code>observableName</code> value.
     * If no nodes can be found with the given observableName, then null is returned.
     * @param node The parent node to check (including the children of the node)
     * @param observableName The name of the observable to look for
     */
    Node findNode(Node node, String observableName) {
        Node returnNode = null
        if(node.id == observableName) {
            log.debug("found case where node.id [${node.id}] == observableName [${observableName}]")
            return node
        } else {
            if (node instanceof Parent) {
                for(Node loopNode : (node as Parent).getChildrenUnmodifiable()) {
                    Node tempNode = findNode(loopNode, observableName)
                    if(tempNode != null) {
                        return tempNode
                    }
                }
            }
        }
        returnNode
    }

    /**
     * Base initialization of a view.  Creates the {@link Scene} object, and populates it with
     * the default FXML (which matches the package and class name); connects the actions within
     * the FXML to the controller; and binds the FXML elements to the model fields.
     * @param controller The controller to connect the FXML actions to
     * @param model The model to bind the FXML elements to
     */
    void baseInitUI(GriffonController controller, GriffonModel model) {
        // custom build the scene, loading from FXML
        scene = new Scene(new Group())
        scene.setFill(Color.WHITE)

        Node node = loadFromFXML()
        ((Group) scene.getRoot()).getChildren().addAll(node)

        connectActions(node, controller)

        bindings = autoBind(node, model)

        // make sure the GriffonConfig is ready to go
        if(!GriffonConfig.griffonApplication) {
            GriffonConfig.griffonApplication = this.application
        }

        // queue up the elements of this mvc-group to be autowired later
        GriffonConfig.queueForAutowire(controller)
        GriffonConfig.queueForAutowire(model)
        GriffonConfig.queueForAutowire(this)
    }

    /**
     * Embeds the UI elements from the scene identified by the {@link SceneDefinition}
     * into the given {@link AnchorPane}
     * @param anchorPane The anchorPane that will host the component
     * @param sceneLabel The label that identifies a scene to embed
     */
    void setSceneInAnchorPane(AnchorPane anchorPane, SceneDefinition sceneLabel) {
        anchorPane.children.addAll(sceneManager.getScene(sceneLabel)?.root?.childrenUnmodifiable)
    }

}
