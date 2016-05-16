begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Repeat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|DateRangePrefixTree
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|NumberRangePrefixTree
operator|.
name|UnitNRShape
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|query
operator|.
name|SpatialOperation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Shape
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomBoolean
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomIntBetween
import|;
end_import

begin_class
DECL|class|DateNRStrategyTest
specifier|public
class|class
name|DateNRStrategyTest
extends|extends
name|RandomSpatialOpStrategyTestCase
block|{
DECL|field|ITERATIONS
specifier|static
specifier|final
name|int
name|ITERATIONS
init|=
literal|10
decl_stmt|;
DECL|field|tree
name|DateRangePrefixTree
name|tree
decl_stmt|;
DECL|field|randomCalWindowMs
name|long
name|randomCalWindowMs
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|tree
operator|=
name|DateRangePrefixTree
operator|.
name|INSTANCE
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|strategy
operator|=
operator|new
name|NumberRangePrefixTreeStrategy
argument_list|(
name|tree
argument_list|,
literal|"dateRange"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//Test the format that existed<= Lucene 5.0
name|strategy
operator|=
operator|new
name|NumberRangePrefixTreeStrategy
argument_list|(
name|tree
argument_list|,
literal|"dateRange"
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|CellToBytesRefIterator
name|newCellToBytesRefIterator
parameter_list|()
block|{
return|return
operator|new
name|CellToBytesRefIterator50
argument_list|()
return|;
block|}
block|}
expr_stmt|;
block|}
name|Calendar
name|tmpCal
init|=
name|tree
operator|.
name|newCal
argument_list|()
decl_stmt|;
name|int
name|randomCalWindowField
init|=
name|randomIntBetween
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|,
name|Calendar
operator|.
name|MILLISECOND
argument_list|)
decl_stmt|;
name|tmpCal
operator|.
name|add
argument_list|(
name|randomCalWindowField
argument_list|,
literal|2_000
argument_list|)
expr_stmt|;
name|randomCalWindowMs
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|2000L
argument_list|,
name|tmpCal
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|ITERATIONS
argument_list|)
DECL|method|testIntersects
specifier|public
name|void
name|testIntersects
parameter_list|()
throws|throws
name|IOException
block|{
name|testOperationRandomShapes
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|ITERATIONS
argument_list|)
DECL|method|testWithin
specifier|public
name|void
name|testWithin
parameter_list|()
throws|throws
name|IOException
block|{
name|testOperationRandomShapes
argument_list|(
name|SpatialOperation
operator|.
name|IsWithin
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|ITERATIONS
argument_list|)
DECL|method|testContains
specifier|public
name|void
name|testContains
parameter_list|()
throws|throws
name|IOException
block|{
name|testOperationRandomShapes
argument_list|(
name|SpatialOperation
operator|.
name|Contains
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithinSame
specifier|public
name|void
name|testWithinSame
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Calendar
name|cal
init|=
name|tree
operator|.
name|newCal
argument_list|()
decl_stmt|;
name|testOperation
argument_list|(
name|tree
operator|.
name|toShape
argument_list|(
name|cal
argument_list|)
argument_list|,
name|SpatialOperation
operator|.
name|IsWithin
argument_list|,
name|tree
operator|.
name|toShape
argument_list|(
name|cal
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//is within itself
block|}
annotation|@
name|Test
DECL|method|testWorld
specifier|public
name|void
name|testWorld
parameter_list|()
throws|throws
name|IOException
block|{
name|testOperation
argument_list|(
name|tree
operator|.
name|toShape
argument_list|(
name|tree
operator|.
name|newCal
argument_list|()
argument_list|)
argument_list|,
comment|//world matches everything
name|SpatialOperation
operator|.
name|Contains
argument_list|,
name|tree
operator|.
name|toShape
argument_list|(
name|randomCalendar
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBugInitIterOptimization
specifier|public
name|void
name|testBugInitIterOptimization
parameter_list|()
throws|throws
name|Exception
block|{
comment|//bug due to fast path initIter() optimization
name|testOperation
argument_list|(
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[2014-03-27T23 TO 2014-04-01T01]"
argument_list|)
argument_list|,
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|tree
operator|.
name|parseShape
argument_list|(
literal|"[2014-04 TO 2014-04-01T02]"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|randomIndexedShape
specifier|protected
name|Shape
name|randomIndexedShape
parameter_list|()
block|{
name|Calendar
name|cal1
init|=
name|randomCalendar
argument_list|()
decl_stmt|;
name|UnitNRShape
name|s1
init|=
name|tree
operator|.
name|toShape
argument_list|(
name|cal1
argument_list|)
decl_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
return|return
name|s1
return|;
block|}
try|try
block|{
name|Calendar
name|cal2
init|=
name|randomCalendar
argument_list|()
decl_stmt|;
name|UnitNRShape
name|s2
init|=
name|tree
operator|.
name|toShape
argument_list|(
name|cal2
argument_list|)
decl_stmt|;
if|if
condition|(
name|cal1
operator|.
name|compareTo
argument_list|(
name|cal2
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
name|tree
operator|.
name|toRangeShape
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|tree
operator|.
name|toRangeShape
argument_list|(
name|s2
argument_list|,
name|s1
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
assert|assert
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Differing precision"
argument_list|)
assert|;
return|return
name|s1
return|;
block|}
block|}
DECL|method|randomCalendar
specifier|private
name|Calendar
name|randomCalendar
parameter_list|()
block|{
name|Calendar
name|cal
init|=
name|tree
operator|.
name|newCal
argument_list|()
decl_stmt|;
name|cal
operator|.
name|setTimeInMillis
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
operator|%
name|randomCalWindowMs
argument_list|)
expr_stmt|;
try|try
block|{
name|tree
operator|.
name|clearFieldsAfter
argument_list|(
name|cal
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Calendar
operator|.
name|FIELD_COUNT
operator|+
literal|1
argument_list|)
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Calendar underflow"
argument_list|)
condition|)
throw|throw
name|e
throw|;
block|}
return|return
name|cal
return|;
block|}
annotation|@
name|Override
DECL|method|randomQueryShape
specifier|protected
name|Shape
name|randomQueryShape
parameter_list|()
block|{
return|return
name|randomIndexedShape
argument_list|()
return|;
block|}
block|}
end_class

end_unit

