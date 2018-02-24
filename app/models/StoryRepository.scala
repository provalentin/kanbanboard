package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for stories.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class StoryRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
   * Here we define the table. It will have a story title
   */
  private class StoryTable(tag: Tag) extends Table[Story](tag, "stories") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The name column */
    def title = column[String]("title")

    /** The age column */
    def phase = column[String]("phase")

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the Story object.
     *
     * In this case, we are simply passing the id, title and phase parameters to the Story case classes
     * apply and unapply methods.
     */
    def * = (id, title, phase) <> ((Story.apply _).tupled, Story.unapply)
  }

  /**
   * The starting point for all queries on the story table.
   */
  private val people = TableQuery[StoryTable]

  /**
   * Create a person with the given title and phase.
   *
   * This is an asynchronous operation, it will return a future of the created person, which can be used to obtain the
   * id for that person.
   */
  def create(title: String, phase: String): Future[Story] = db.run {
    // We create a projection of just the name and age columns, since we're not inserting a value for the id column
    (people.map(p => (p.title, p.phase))
      // Now define it to return the id, because we want to know what id was generated for the story
      returning people.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((tuple, id) => Story(id, tuple._1, tuple._2))
    // And finally, insert the story into the database
    ) += (title, phase)
  }

  /**
   * List all the people in the database.
   */
  def list(): Future[Seq[Story]] = db.run {
    people.result
  }
}
