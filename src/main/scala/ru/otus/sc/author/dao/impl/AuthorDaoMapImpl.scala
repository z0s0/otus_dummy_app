package ru.otus.sc.author.dao.impl

import java.util.UUID

import ru.otus.sc.author.dao.AuthorDao
import ru.otus.sc.author.model.Author

import scala.concurrent.{ExecutionContext, Future}

class AuthorDaoMapImpl(implicit val ThreadPool: ExecutionContext) extends AuthorDao {
  private var authors = Map[UUID, Author]()

  override def listAuthors: Future[Vector[Author]]         = Future(authors.values.toVector)
  override def getAuthor(id: UUID): Future[Option[Author]] = Future(authors.get(id))

  override def createAuthor(author: Author): Future[Option[Author]] =
    Future {
      val id           = author.id.getOrElse(UUID.randomUUID())
      val authorWithID = author.copy(id = Some(id))

      if (isAuthorValid(authorWithID)) {
        authors += (id -> authorWithID)
        Some(authorWithID)
      } else None
    }

  override def updateAuthor(author: Author): Future[Option[Author]] =
    Future {
      if (isAuthorValid(author)) {
        for {
          id <- author.id
          _  <- authors.get(id)
        } yield {
          authors += (id -> author)
          author
        }
      } else None
    }

  override def deleteAuthor(id: UUID): Future[Option[Author]] =
    Future {
      authors.get(id) match {
        case a @ Some(_) =>
          authors -= id
          a

        case None => None
      }
    }

  private def isAuthorValid(author: Author): Boolean = true
}
