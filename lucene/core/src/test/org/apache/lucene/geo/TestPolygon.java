begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.geo
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
package|;
end_package

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

begin_class
DECL|class|TestPolygon
specifier|public
class|class
name|TestPolygon
extends|extends
name|LuceneTestCase
block|{
comment|/** null polyLats not allowed */
DECL|method|testPolygonNullPolyLats
specifier|public
name|void
name|testPolygonNullPolyLats
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|Polygon
argument_list|(
literal|null
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|66
operator|,
operator|-
literal|65
operator|,
operator|-
literal|65
operator|,
operator|-
literal|66
operator|,
operator|-
literal|66
block|}
argument_list|)
decl_stmt|;
block|}
block|)
class|;
end_class

begin_expr_stmt
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"polyLats must not be null"
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
unit|}
comment|/** null polyLons not allowed */
end_comment

begin_function
DECL|method|testPolygonNullPolyLons
unit|public
name|void
name|testPolygonNullPolyLons
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
literal|18
operator|,
literal|18
operator|,
literal|19
operator|,
literal|19
operator|,
literal|18
block|}
operator|,
literal|null
argument_list|)
decl_stmt|;
block|}
end_function

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"polyLons must not be null"
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
unit|}
comment|/** polygon needs at least 3 vertices */
end_comment

begin_function
DECL|method|testPolygonLine
unit|public
name|void
name|testPolygonLine
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
literal|18
operator|,
literal|18
operator|,
literal|18
block|}
operator|,
operator|new
name|double
index|[]
block|{
operator|-
literal|66
operator|,
operator|-
literal|65
operator|,
operator|-
literal|66
block|}
argument_list|)
decl_stmt|;
block|}
end_function

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"at least 4 polygon points required"
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
unit|}
comment|/** polygon needs same number of latitudes as longitudes */
end_comment

begin_function
DECL|method|testPolygonBogus
unit|public
name|void
name|testPolygonBogus
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
literal|18
operator|,
literal|18
operator|,
literal|19
operator|,
literal|19
block|}
operator|,
operator|new
name|double
index|[]
block|{
operator|-
literal|66
operator|,
operator|-
literal|65
operator|,
operator|-
literal|65
operator|,
operator|-
literal|66
operator|,
operator|-
literal|66
block|}
argument_list|)
decl_stmt|;
block|}
end_function

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"must be equal length"
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
unit|}
comment|/** polygon must be closed */
end_comment

begin_function
DECL|method|testPolygonNotClosed
unit|public
name|void
name|testPolygonNotClosed
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
literal|18
operator|,
literal|18
operator|,
literal|19
operator|,
literal|19
operator|,
literal|19
block|}
operator|,
operator|new
name|double
index|[]
block|{
operator|-
literal|66
operator|,
operator|-
literal|65
operator|,
operator|-
literal|65
operator|,
operator|-
literal|66
operator|,
operator|-
literal|67
block|}
argument_list|)
decl_stmt|;
block|}
end_function

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"it must close itself"
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

unit|} }
end_unit
