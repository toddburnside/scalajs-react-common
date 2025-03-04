package react.common

import scala.scalajs.js
import scala.scalajs.js.|
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.facade.JsNumber
import japgolly.scalajs.react.facade.React
import japgolly.scalajs.react.vdom.VdomNode

package syntax {
  trait EnumValueSyntax {
    implicit def syntaxEnumValue[A: EnumValue](a: A): EnumValueOps[A] =
      new EnumValueOps(a)

    implicit def syntaxEnumValue[A: EnumValue](a: js.UndefOr[A]): EnumValueUndefOps[A] =
      new EnumValueUndefOps(a)

    implicit def syntaxEnumValueB[A: EnumValueB](a: A): EnumValueOpsB[A] =
      new EnumValueOpsB(a)

    implicit def syntaxEnumValueB[A: EnumValueB](a: js.UndefOr[A]): EnumValueUndefOpsB[A] =
      new EnumValueUndefOpsB(a)
  }

  final class EnumValueOps[A](a: A)(implicit ev: EnumValue[A]) {
    def toJs: String = ev.value(a)
  }

  final class EnumValueUndefOps[A](a: js.UndefOr[A])(implicit ev: EnumValue[A]) {
    def toJs: js.UndefOr[String] =
      a.map(ev.value)
  }

  final class EnumValueOpsB[A](a: A)(implicit ev: EnumValueB[A]) {
    def toJs: Boolean | String = ev.value(a)
  }

  final class EnumValueUndefOpsB[A](a: js.UndefOr[A])(implicit ev: EnumValueB[A]) {
    def toJs: js.UndefOr[Boolean | String] =
      a.map(ev.value)
  }

  trait CallbackPairSyntax extends CallbackSyntax {
    implicit def syntaxCallbackPair1[A](
      a: (js.UndefOr[A => Callback], js.UndefOr[Callback])
    ): CallbackPairOps1[A] =
      new CallbackPairOps1(a._1, a._2)

    implicit def syntaxCallbackPair2[A, B](
      a: (js.UndefOr[(A, B) => Callback], js.UndefOr[Callback])
    ): CallbackPairOps2[A, B] =
      new CallbackPairOps2(a._1, a._2)
  }

  final class VdomOps(val node: VdomNode) extends AnyVal {
    def toRaw: React.Node = node.rawNode
  }

  final class VdomUndefOps(val c: js.UndefOr[VdomNode]) extends AnyVal {
    def toJs: js.UndefOr[React.Node] = c.map(_.rawNode)
  }

  // Some useful conversions
  final class CallbackOps(val c: js.UndefOr[Callback]) extends AnyVal {
    def toJs: js.UndefOr[js.Function0[Unit]]              = c.map(x => () => x.runNow())
    def toJs1[A]: js.UndefOr[js.Function1[A, Unit]]       = c.map(x => (_: A) => x.runNow())
    def toJs2[A, B]: js.UndefOr[js.Function2[A, B, Unit]] = c.map(x => (_: A, _: B) => x.runNow())
  }

  final class CallbackOps1[A](val c: js.UndefOr[A => Callback]) extends AnyVal {
    def toJs: js.UndefOr[js.Function1[A, Unit]] = c.map(x => (a: A) => x(a).runNow())
  }

  final class CallbackOps2[A, B](val c: js.UndefOr[(A, B) => Callback]) extends AnyVal {
    def toJs: js.UndefOr[js.Function2[A, B, Unit]] = c.map(x => (a: A, b: B) => x(a, b).runNow())
  }

  trait CallbackSyntax {
    implicit def callbackOps(c: js.UndefOr[Callback]): CallbackOps =
      new CallbackOps(c)

    implicit def callbackOps1[A](c: js.UndefOr[A => Callback]): CallbackOps1[A] =
      new CallbackOps1(c)

    implicit def callbackOps2[A, B](c: js.UndefOr[(A, B) => Callback]): CallbackOps2[A, B] =
      new CallbackOps2(c)

    final class CallbackPairOps1[A](a: js.UndefOr[A => Callback], b: js.UndefOr[Callback]) {
      def toJs: js.UndefOr[js.Function1[A, Unit]] = a.toJs.orElse(b.toJs1)
    }

    final class CallbackPairOps2[A, B](a: js.UndefOr[(A, B) => Callback], b: js.UndefOr[Callback]) {
      def toJs: js.UndefOr[js.Function2[A, B, Unit]] = a.toJs.orElse(b.toJs2)
    }
  }

  final class JsNumberOps(val d: JsNumber) extends AnyVal {
    // Some uglies for js union types
    def toDouble: Double =
      (d: Any) match {
        case d: Float  => d.toDouble
        case d: Double => d
        case d: Byte   => d.toDouble
        case d: Short  => d.toDouble
        case d: Int    => d.toDouble
        case _         => sys.error("Unsupported type")
      }

    // Some uglies for js union types
    def toInt: Int =
      (d: Any) match {
        case d: Float  => d.toInt
        case d: Double => d.toInt
        case d: Byte   => d.toInt
        case d: Short  => d.toInt
        case d: Int    => d
        case _         => sys.error("Unsupported type")
      }
  }

  trait AllSyntax
      extends EnumValueSyntax
      with CallbackPairSyntax
      with style.StyleSyntax
      with RenderSyntax
      with VdomSyntax {
    implicit def vdomOps(node: VdomNode): VdomOps =
      new VdomOps(node)

    implicit def vdomUndefOps(node: js.UndefOr[VdomNode]): VdomUndefOps =
      new VdomUndefOps(node)

    implicit def jsNumberOps(d: JsNumber): JsNumberOps =
      new JsNumberOps(d)

  }

  trait RenderSyntax {
    // Render(Fn) conversions
    implicit def GenericFnComponentP2RenderFn[P <: js.Object](
      p: GenericFnComponentP[P]
    ): RenderFn[P] =
      p.render

    implicit def GenericFnComponentPC2RenderFn[P <: js.Object](
      p: GenericFnComponentPC[P, _]
    ): RenderFn[P] =
      p.render

    implicit def GenericFnComponentPA2RenderFn[P <: js.Object](
      p: GenericFnComponentPA[P, _]
    ): RenderFn[P] =
      p.render

    implicit def GenericFnComponentPAC2RenderFn[P <: js.Object](
      p: GenericFnComponentPAC[P, _]
    ): RenderFn[P] =
      p.render

    // Render conversions
    implicit def GenericComponentP2Render[P <: js.Object](
      p: GenericComponentP[P]
    ): Render[P] =
      p.render

    implicit def GenericComponentPC2Render[P <: js.Object](
      p: GenericComponentPC[P, _]
    ): Render[P] =
      p.render

    implicit def GenericComponentPA2Render[P <: js.Object](
      p: GenericComponentPA[P, _]
    ): Render[P] =
      p.render

    implicit def GenericComponentPAC2Render[P <: js.Object](
      p: GenericComponentPAC[P, _]
    ): Render[P] =
      p.render
  }

  trait VdomSyntax    {
    // Fn to VdomNode conversions
    implicit def GenericFnComponentP2VdomNode[P <: js.Object](
      p: GenericFnComponentP[P]
    ): VdomNode =
      p.render

    implicit def GenericFnComponentPC2VdomNode[P <: js.Object](
      p: GenericFnComponentPC[P, _]
    ): VdomNode =
      p.render

    implicit def GenericFnComponentPA2VdomNode[P <: js.Object](
      p: GenericFnComponentPA[P, _]
    ): VdomNode =
      p.render

    implicit def GenericFnComponentPAC2VdomNode[P <: js.Object](
      p: GenericFnComponentPAC[P, _]
    ): VdomNode =
      p.render

    // Component 2 VdomNode
    implicit def GenericComponentP2VdomNode[P <: js.Object](
      p: GenericComponentP[P]
    ): VdomNode =
      p.render

    implicit def GenericComponentPC2VdomNode[P <: js.Object](
      p: GenericComponentPC[P, _]
    ): VdomNode =
      p.render

    implicit def GenericComponentPA2VdomNode[P <: js.Object](
      p: GenericComponentPA[P, _]
    ): VdomNode =
      p.render

    implicit def GenericComponentPAC2VdomNode[P <: js.Object](
      p: GenericComponentPAC[P, _]
    ): VdomNode =
      p.render

    implicit def GenericFnComponentP2UndefVdomNode[P <: js.Object](
      p: GenericFnComponentP[P]
    ): js.UndefOr[VdomNode] =
      p.render: VdomNode

    implicit def GenericFnComponentPC2UndefVdomNode[P <: js.Object](
      p: GenericFnComponentPC[P, _]
    ): js.UndefOr[VdomNode] =
      p.render: VdomNode

    implicit def GenericFnComponentPA2UndefVdomNode[P <: js.Object](
      p: GenericFnComponentPA[P, _]
    ): js.UndefOr[VdomNode] =
      p.render: VdomNode

    implicit def GenericFnComponentPAC2UndefVdomNode[P <: js.Object](
      p: GenericFnComponentPAC[P, _]
    ): js.UndefOr[VdomNode] =
      p.render: VdomNode

    implicit def GenericComponentP2UndefVdomNode[P <: js.Object](
      p: GenericComponentP[P]
    ): js.UndefOr[VdomNode] =
      p.render: VdomNode

    implicit def GenericComponentPC2UndefVdomNode[P <: js.Object](
      p: GenericComponentPC[P, _]
    ): js.UndefOr[VdomNode] =
      p.render: VdomNode

    implicit def GenericComponentPA2UndefVdomNode[P <: js.Object](
      p: GenericComponentPA[P, _]
    ): js.UndefOr[VdomNode] =
      p.render: VdomNode

    implicit def GenericComponentPAC2UndefVdomNode[P <: js.Object](
      p: GenericComponentPAC[P, _]
    ): js.UndefOr[VdomNode] =
      p.render: VdomNode
    // End VdomNode conversions
  }
}

package object syntax {
  object all    extends AllSyntax
  object vdom   extends VdomSyntax
  object render extends RenderSyntax
}
