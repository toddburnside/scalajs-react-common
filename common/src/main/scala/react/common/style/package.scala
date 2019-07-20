package react

package common

import scala.scalajs.js
import scala.scalajs.js.|
import js.JSConverters._
import japgolly.scalajs.react.vdom.html_<^._

package object style {
  implicit val IntStyleExtractor: StyleExtractor[Int] = new StyleExtractor[Int] {
    override def extract(s: Style, key: String): Option[Int] =
      s.styles.get(key).flatMap { x =>
        (x: Any) match {
          case x: Int => Some(x)
          case _      => None
        }
      }
  }

  implicit val StringStyleExtractor: StyleExtractor[String] = new StyleExtractor[String] {
    override def extract(s: Style, key: String): Option[String] =
      s.styles.get(key).flatMap { x =>
        (x: Any) match {
          case x: String => Some(x)
          case _         => None
        }
      }
  }
}

package style {
  sealed trait StyleExtractor[A] {
    def extract(s: Style, key: String): Option[A]
  }

  final class StyleOps(val s: Style) extends AnyVal {
    def toJsObject: js.Object = Style.toJsObject(s)

    def extract[A](key: String)(implicit S: StyleExtractor[A]): Option[A] =
      S.extract(s, key)

    def remove(key: String): Style =
      if (s.styles.contains(key)) {
        Style(s.styles - key)
      } else {
        s
      }

  }

  final class ClassnameCssOps(cn: (js.UndefOr[String], js.UndefOr[Css])) {

    def toJs: js.UndefOr[String] = (cn._1.toOption, cn._2.toOption) match {
      case (cn @ Some(_), None) => cn.orUndefined
      case (None, cz @ Some(_)) => cz.map(_.htmlClass).orUndefined
      case (Some(cs), Some(cz)) => s"$cs ${cz.htmlClass}"
      case _                    => js.undefined
    }

  }

  trait StyleSyntax {
    implicit def styePairU(a: (js.UndefOr[String], js.UndefOr[Css])): ClassnameCssOps =
      new ClassnameCssOps(a)

    implicit def styeOps(s: Style): StyleOps =
      new StyleOps(s)

    implicit final def cssToTagMod(s: Css): TagMod =
      ^.className := s.htmlClass

    implicit final def listCssToTagMod(s: List[Css]): TagMod =
      s.map(cssToTagMod).toTagMod
  }

  /**
    * Simple class to represent styles
    */
  final case class Style(styles: Map[String, String | Int])

  object Style {

    def toJsObject(style: Style): js.Object =
      style.styles.toJSDictionary.asInstanceOf[js.Object]

    def fromJsObject(o: js.Object): Style = {
      val xDict = o.asInstanceOf[js.Dictionary[String | Int]]
      val map   = (for ((prop, value) <- xDict) yield prop -> value).toMap
      Style(map)
    }
  }

  /**
    * Simple class to represent css class
    */
  final case class Css(htmlClasses: List[String]) {
    val htmlClass: String = htmlClasses.mkString(" ")
  }

  object Css {
    def apply(htmlClass: String): Css = Css(List(htmlClass))

    val Zero: Css = Css(Nil)
  }
}
