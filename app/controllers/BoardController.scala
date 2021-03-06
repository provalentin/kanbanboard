package controllers

import javax.inject._

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class BoardController @Inject()(repo: StoryRepository,
                                  cc: MessagesControllerComponents
                                )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  /**
   * The mapping for the story form.
   */
  val storyForm: Form[CreateStoryForm] = Form {
    mapping(
      "title" -> nonEmptyText,
      "phase" -> text
    )(CreateStoryForm.apply)(CreateStoryForm.unapply)
  }
    
  /**
   * A REST endpoint that gets all the story as JSON.
   */
  def all = Action.async { implicit request =>
    repo.list().map { s =>
      Ok(views.html.board(Json.toJson(s).toString)(s))
    }
  }
    
  /**
   * The index action.
   */
  def createStory = Action { implicit request =>
    Ok(views.html.addstory(storyForm))
  }
  
  /**
   * The add person action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on PersonRepository.
   */
  def addStory = Action.async { implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle succes.
    storyForm.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the person creation function returns a future.
      errorForm => {
        Future.successful(Ok(views.html.addstory(errorForm)))
      },
      // There were no errors in the from, so create the person.
      story => {
        repo.create(story.title, story.phase).map { _ =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.BoardController.all).flashing("success" -> "user.created")
        }
      }
    )
  }
  
  def moveStory = Action.async { implicit request =>
    val storyId = request.body.asFormUrlEncoded.get("id").map(_.head)
    val id = storyId.head.asDigit
    repo.updateStatus(id, "test")
    Future.successful(Redirect(routes.BoardController.all).flashing("success" -> "card.updated"))
  }

}   
    
 /**
 * The create story form.
 *
 * Generally for forms, you should define separate objects to your models, since forms very often need to present data
 * in a different way to your models.  In this case, it doesn't make sense to have an id parameter in the form, since
 * that is generated once it's created.
 */
case class CreateStoryForm(title: String, phase: String)   
