/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.play.events.monitoring

import org.mockito.ArgumentMatchers.isA
import org.mockito.hamcrest.MockitoHamcrest.argThat

import java.util.concurrent.TimeUnit
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers
import org.hamcrest._
import org.scalatest.wordspec.AnyWordSpecLike
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.events.handlers.EventHandler
import uk.gov.hmrc.play.events.{AlertCode, Unknown}

import scala.concurrent.ExecutionContext.Implicits.global

class TimerSpec extends AnyWordSpecLike with MockitoSugar with Matchers {

  implicit val hc: HeaderCarrier = new HeaderCarrier()

  val TolerancePercentage = 10

  val TimeToSleep: FiniteDuration = 1000 * 1000 * 1000 nanos

  def testFuture: Future[AlertCode] = Future{
    TimeUnit.NANOSECONDS.sleep(TimeToSleep.length)

    "Hello"
  }


  "Timer" should {

    "generate Monitor events containing call duration with NO alert code" in new Timer {
      override def source: String = "This-Test"

      val mockHandler: EventHandler = mock[EventHandler]

      override def eventHandlers: Set[EventHandler] = Set(mockHandler)

      val result: AlertCode = Await.result(

        timer()(testFuture),
        10 seconds
      )

      result shouldBe "Hello"

      verify(mockHandler).handle(argThat(new DefaultTimerEventMatcher(source, Unknown, TimeToSleep, TolerancePercentage)))(isA(classOf[HeaderCarrier]), isA(classOf[ExecutionContext]))
    }

    "generate Monitor events containing call duration with alert code" in new Timer {
      override def source: String = "This-Test"

      val mockHandler: EventHandler = mock[EventHandler]

      override def eventHandlers: Set[EventHandler] = Set(mockHandler)

      val result: AlertCode = Await.result(

        timer("test-code")(testFuture),
        10 seconds
      )

      result shouldBe "Hello"

      verify(mockHandler).handle(argThat(new DefaultTimerEventMatcher(source, "test-code", TimeToSleep, TolerancePercentage)))(isA(classOf[HeaderCarrier]), isA(classOf[ExecutionContext]))
    }
  }
}

class DefaultTimerEventMatcher(source: String, alertCode: AlertCode, expected: Duration, tolerancePercentage: Long) extends TypeSafeMatcher[DefaultTimerEvent] {

  private val Unit = "NANOSECONDS"

  private val toleranceRatio = tolerancePercentage.abs.toDouble / 100
  private val toleranceDelta = (expected.toNanos * toleranceRatio).round

  private val lowerBound = expected.toNanos - toleranceDelta
  private val upperBound = expected.toNanos + toleranceDelta

  def matchesSafely(item: DefaultTimerEvent): Boolean = {

    val sourceMatches = item.source == source

    val alertCodeMatches = item.name == s"Timer-$alertCode"

    val unitMatches = item.data("Unit") == Unit

    val elapsed = item.data("Duration").toLong

    val withinBounds = elapsed >= lowerBound && elapsed <= upperBound

    sourceMatches && alertCodeMatches && unitMatches && withinBounds
  }

  def describeTo(description: Description): Unit = description.appendText(f"DefaultTimerEvent($source,Timer-$alertCode,")
                                                              .appendText(f"Map(Duration -> ${expected.toNanos.toDouble}%,.0f±${toleranceDelta.toDouble}%,.0f, Unit -> $Unit))")
                                                              .appendText("\n")
                                                              .appendText(f" ${expected.toNanos.toDouble}%,.0f±${toleranceDelta.toDouble}%,.0f means that we want the duration")
                                                              .appendText(f" in nanoseconds to be between ${lowerBound.toDouble}%,.0f and ${upperBound.toDouble}%,.0f inclusive")
                                                              .appendText(f" (the tolerance is currently set to $tolerancePercentage percent either side of")
                                                              .appendText(f" ${expected.toNanos.toDouble}%,.0f nanoseconds)")
}
