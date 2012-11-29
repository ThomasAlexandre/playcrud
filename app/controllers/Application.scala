package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import views._
import utilities._
import StringUtil._
import java.io.{ File, FileWriter }

object Application extends Controller {

  val config = Config()
  val dbReader = new DBReader(config)

  /**
   * Describes the hello form.
   */
  val dbForm = Form(
    tuple(
      "driver" -> nonEmptyText,
      "dburl" -> nonEmptyText,
      "username" -> nonEmptyText,
      "password" -> nonEmptyText))

  // -- Actions

  /**
   * Home page
   */
  def index = Action {
    Ok(html.index(dbForm))
  }

  /**
   * Generate a domain entity, its controller and views (to list, create, edit, delete)
   */
  def generateAll = Action { implicit request =>
    dbForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.index(formWithErrors)),
      {
        case (driver, dburl, username, password) =>

          val allProperties = dbReader.getProperties()
          Logger.info("properties: " + allProperties)

          // val tablenames = allProperties.keys
          val tablenames = dbReader.getTables().keys

          Logger.info("tablenames: " + tablenames)

          // Generate pages concerning the whole application
          // List of pairs of (filepath info, output as text)
          lazy val filesFromPlayProject = List(
            "/conf/routes" -> txt.routes(tablenames.toList).toString,
            "/conf/application.conf" -> txt.conf(tablenames.toList).toString,
            "/conf/messages" -> txt.messages(tablenames.toList).toString,
            "/app/Global.scala" -> txt.global(tablenames.toList).toString,
            "/app/models/AllModels.scala" -> txt.allmodels(tablenames.toList).toString,
            "/app/controllers/Application.scala" -> txt.appli(tablenames.toList).toString,
            "/app/views/twitterBootstrapInput.scala.html" -> txt.twitterBootstrapInput(tablenames.toList).toString,
            "/app/views/main.scala.html" -> txt.main(tablenames.toList).toString,
            "/app/views/controllerlist.scala.html" -> txt.controllerlist(tablenames.toList).toString,
            "/project/Build.scala" -> txt.build(tablenames.toList,config.baseDirectory).toString,
            "/project/plugins.sbt" -> txt.plugins(tablenames.toList).toString,
            "/public/stylesheets/bootstrap.min.css" -> txt.bootstrapmincss(tablenames.toList).toString,
            "/public/stylesheets/main.css" -> txt.maincss(tablenames.toList).toString)

           def createDirectoryIfNeeded(filePath:String): Unit = {
            val fileDirectory = filePath.split("/").reverse.tail.reverse.mkString("/")
            val theDir = new File(fileDirectory)
            
            // if the directory does not exist, create it
            if (!theDir.exists()) {
              Logger.info("creating directory: " + fileDirectory)
              theDir.mkdir()
              }
            } 
            
          /**
           * Help method to write artifacts to a file on disk.
           * @param files a list containing some path information and the generated output
           * @param outputDirectory the root catalog of the target play project 
           */
          def generate(files: List[(String, String)], outputDirectory: String): List[String] =
            for (
              (filepath, output) <- files
            ) yield {
              val absoluteFilename = outputDirectory + filepath      
              createDirectoryIfNeeded(absoluteFilename)
              val out = new FileWriter(outputDirectory + filepath)
              try {
                out.write(output)
              } catch {
                case e: Exception => {
                  Logger.info("Failed to write file: " + e.getStackTrace)
                }
              } finally {
                out.close()
              }
              absoluteFilename
            }

          val generatedFiles = generate(filesFromPlayProject, config.baseDirectory)

          // Generate a domain entity and a controller for each table, as well as several views
          val artifacts = tablenames map { tablename =>
            val primaryKeys = dbReader.getPrimaryKeys(tablename)
            Logger.info("primary keys: " + primaryKeys)

            val foreignKeys = dbReader.getForeignKeys(tablename)

            val tableProps = allProperties(tablename) map { prop =>
              val propertyName = camelifyMethod(prop(3).toString)
              val isPK = primaryKeys.contains(propertyName)
              val isFK = foreignKeys.keys.toList.contains(prop(3).toString)
              val propType = DBUtil.mapping(prop(5).toString)
              TableProperty(
                name = propertyName,
                propertyType = propType,
                nullable = prop(17).toString match {
                  case "NO" => false
                  case "YES" => true
                },
                isPrimaryKey = isPK,
                length = prop(6).toString.toInt,
                dbColumnName = prop(3).toString,
                formattedType = if (isPK) "Pk[" + propType + "]" else propType,
                default = if (isPK) " = NotAssigned" else "",
                isForeignKey = isFK,
                fKeyInfo = if (isFK) foreignKeys(prop(3).toString).head else Vector.empty)
            }
            Logger.info(tablename + " tableProps: " + tableProps)

            def assignEntityNameFromTableName(tablename: String): String = {
              camelify(tablename.reverse.tail.reverse.toLowerCase)
            }

            val entityName = assignEntityNameFromTableName(tablename)

            val mvcFromPlayProject: List[(String, String)] = List(
              s"/app/models/${camelify(tablename.toLowerCase)}.scala" -> txt.entity(tablename, entityName, tableProps).toString,
              s"/app/controllers/${camelify(tablename.toLowerCase)}Controller.scala" -> txt.controller(tablename, entityName, tableProps).toString,
              s"/app/views/${camelify(tablename).toLowerCase}/createForm.scala.html" -> txt.createForm(tablename, entityName, tableProps).toString,
              s"/app/views/${camelify(tablename).toLowerCase}/list.scala.html" -> txt.list(tablename, entityName, tableProps).toString,
              s"/app/views/${camelify(tablename).toLowerCase}/editForm.scala.html" -> txt.editForm(tablename, entityName, tableProps).toString())

            generate(mvcFromPlayProject, config.baseDirectory)
          }
          // temporary for testing
          // Ok("Number of generated mvc artifacts: " + artifacts.size + "\n" + artifacts.toString)
          
          //This should redirect to the generated play app
          Redirect("http://localhost:9000")
      })
  }
}