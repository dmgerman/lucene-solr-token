begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.tier.projections
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|tier
operator|.
name|projections
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|SinusoidalProjectorTest
specifier|public
class|class
name|SinusoidalProjectorTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testProjection
specifier|public
name|void
name|testProjection
parameter_list|()
throws|throws
name|Exception
block|{
name|SinusoidalProjector
name|prj
init|=
operator|new
name|SinusoidalProjector
argument_list|()
decl_stmt|;
comment|//TODO: uncomment once SinusoidalProjector is fixed.  Unfortunately, fixing it breaks a lot of other stuff
comment|/*double[] doubles;     doubles = prj.coords(-89.0, 10);     assertEquals(0.003, doubles[0], 0.001);//x     assertEquals(-89.0 * DistanceUtils.DEGREES_TO_RADIANS, doubles[1]);          doubles = prj.coords(89.0, 0);     assertEquals(0.0, doubles[0]);//x     assertEquals(89.0 * DistanceUtils.DEGREES_TO_RADIANS, doubles[1]);      doubles = prj.coords(89.0, 10);     assertEquals(0.003, doubles[0], 0.001);//x     assertEquals(89.0 * DistanceUtils.DEGREES_TO_RADIANS, doubles[1]);       doubles = prj.coords(-89.0, 0);     assertEquals(0.0, doubles[0]);//x     assertEquals(-89.0 * DistanceUtils.DEGREES_TO_RADIANS, doubles[1]);*/
block|}
block|}
end_class

begin_comment
comment|//This code demonstrates that the SinusoidalProjector is incorrect
end_comment

begin_comment
comment|/*@Test   public void testFoo() throws Exception {     CartesianTierPlotter plotter = new CartesianTierPlotter(11, new SinusoidalProjector(), "foo");     SinusoidalProjector prj = new SinusoidalProjector();     System.out.println("---- Equator ---");     printValues(plotter, prj, 0);     System.out.println("---- North ---");     printValues(plotter, prj, 89.0);     System.out.println("---- South ---");     printValues(plotter, prj, -89.0);   }    private void printValues(CartesianTierPlotter plotter, SinusoidalProjector prj, double latitude){     for (int i = 0; i<= 10; i++){       double boxId = plotter.getTierBoxId(latitude, i);       double[] doubles = prj.coords(latitude, i);       System.out.println("Box[" + latitude + ", " + i + "] = " + boxId + " sinusoidal: [" + doubles[0] + ", " + doubles[1] + "]");     }     for (int i = -10; i<= 0; i++){       double boxId = plotter.getTierBoxId(latitude, i);       double[] doubles = prj.coords(latitude, i);       System.out.println("Box[" + latitude + ", " + i + "] = " + boxId + " sinusoidal: [" + doubles[0] + ", " + doubles[1] + "]");     }    }   */
end_comment

end_unit

