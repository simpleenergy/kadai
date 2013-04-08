/* 
 * Copyright 2012 Atlassian PTY LTD
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
package kadai.concurrent

class PoolSpec extends org.specs2.mutable.SpecificationWithJUnit {

  "Empty pool" should {
    "have an empty busy queue" in { emptyPool.busy.isEmpty must be equalTo true }
    "have an empty available queue" in { emptyPool.available.isEmpty must be equalTo true }
    "not give out objects" in { emptyPool.borrow._2 must beNone }
    "not fail when removing something" in { emptyPool.remove(0) must be equalTo emptyPool }
    "allow to give one back that wasn't there before" in { emptyPool.giveBack(0).available.size must be equalTo 1 }
    "allow to add something" in { 1 :: 2 :: 3 :: emptyPool must be equalTo fullPool }
  }

  "Full pool" should {
    "have an available queue" in { fullPool.available.size must be equalTo 3 }
    "have an empty busy queue" in { fullPool.busy.isEmpty must be equalTo true }
    "give out objects" in { fullPool.borrow._2.get must be equalTo 1 }
    "remove objects" in { fullPool.remove(1).available.size must be equalTo 2 }
    "take objects back" in {
      val pool = fullPool
      pool.giveBack(pool.borrow)
      pool must be equalTo fullPool
    }
    "allow to add something" in { (4 :: fullPool).available.size must be equalTo 4 }
  }

  "Busy pool" should {
    "have a busy queue" in { busyPool.busy.size must be equalTo 1 }
    "have an available queue" in { busyPool.available.size must be equalTo 2 }
    "give out objects" in { busyPool.borrow._2.get must be equalTo 1 }
    "remove available objects" in { busyPool.remove(1).available.size must be equalTo 1 }
    "remove busy objects" in { busyPool.remove(3).busy.isEmpty must be equalTo true }
    "take objects back and have them removed from the busy queue" in { busyPool.giveBack(3).busy.isEmpty must be equalTo true }
    "take objects back and have them returned to the available queue" in { busyPool.giveBack(3).available.size must be equalTo 3 }
  }

  def emptyPool = Pool.empty
  def fullPool = Pool[Int](List(1, 2, 3))
  def busyPool = Pool[Int](List(1, 2), List(3))
}