begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|RandomIndexWriter
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
name|search
operator|.
name|IndexSearcher
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
name|store
operator|.
name|Directory
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_comment
comment|/** Simple tests for {@link LatLonPoint#newDistanceQuery} */
end_comment

begin_class
DECL|class|TestLatLonPointDistanceQuery
specifier|public
class|class
name|TestLatLonPointDistanceQuery
extends|extends
name|LuceneTestCase
block|{
comment|/** test we can search for a point */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// add a doc with a location
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
literal|"field"
argument_list|,
literal|18.313694
argument_list|,
operator|-
literal|65.227444
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// search within 50km and verify we found our doc
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
operator|-
literal|65
argument_list|,
literal|50_000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** negative distance queries are not allowed */
DECL|method|testNegativeRadius
specifier|public
name|void
name|testNegativeRadius
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
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"radiusMeters"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is invalid"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** NaN distance queries are not allowed */
DECL|method|testNaNRadius
specifier|public
name|void
name|testNaNRadius
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
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"radiusMeters"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is invalid"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Inf distance queries are not allowed */
DECL|method|testInfRadius
specifier|public
name|void
name|testInfRadius
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
decl_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"radiusMeters"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is invalid"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"radiusMeters"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is invalid"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

