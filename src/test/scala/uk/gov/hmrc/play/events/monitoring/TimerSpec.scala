/*
 * Copyright 2017 HM Revenue & Customs
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

import java.util.concurrent.TimeUnit

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.hamcrest._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.events.handlers.EventHandler
import uk.gov.hmrc.play.events.{AlertCode, Unknown}

import scala.concurrent.ExecutionContext.Implicits.global

class TimerSpec extends WordSpec with MockitoSugar with Matchers {

  implicit val hc = new HeaderCarrier()

  val TolerancePercentage = 10

  val TimeToSleep = 1000 * 1000 * 1000 nanos

  def testFuture = Future{
    TimeUnit.NANOSECONDS.sleep(TimeToSleep.length)

    "Hello"
  }


  "Timer" should {

    "generate Monitor events containing call duration with NO alert code" in new Timer {
      override def source: String = "This-Test"

      val mockHandler = mock[EventHandler]

      override def eventHandlers = Set(mockHandler)

      val result = Await.result(

        timer()(testFuture),
        10 seconds
      )

      result shouldBe "Hello"

      verify(mockHandler).handle(argThat(new DefaultTimerEventMatcher(source, Unknown, TimeToSleep, TolerancePercentage)))(isA(classOf[HeaderCarrier]), isA(classOf[ExecutionContext]))
    }

    "generate Monitor events containing call duration with alert code" in new Timer {
      override def source: String = "This-Test"

      val mockHandler = mock[EventHandler]

      override def eventHandlers = Set(mockHandler)

      val result = Await.result(

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
                                                              .appendText(f"Map(Duration -> ${expected.toNanos}%,.0f±$toleranceDelta%,.0f, Unit -> $Unit))")
                                                              .appendText("\n")
                                                              .appendText(f" ${expected.toNanos}%,.0f±$toleranceDelta%,.0f means that we want the duration")
                                                              .appendText(f" in nanoseconds to be between $lowerBound%,.0f and $upperBound%,.0f inclusive")
                                                              .appendText(f" (the tolerance is currently set to $tolerancePercentage percent either side of")
                                                              .appendText(f" ${expected.toNanos}%,.0f nanoseconds)")
}
