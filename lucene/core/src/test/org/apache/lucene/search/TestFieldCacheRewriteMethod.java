begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
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
name|automaton
operator|.
name|RegExp
import|;
end_import

begin_comment
comment|/**  * Tests the FieldcacheRewriteMethod with random regular expressions  */
end_comment

begin_class
DECL|class|TestFieldCacheRewriteMethod
specifier|public
class|class
name|TestFieldCacheRewriteMethod
extends|extends
name|TestRegexpRandom2
block|{
comment|/** Test fieldcache rewrite against filter rewrite */
annotation|@
name|Override
DECL|method|assertSame
specifier|protected
name|void
name|assertSame
parameter_list|(
name|String
name|regexp
parameter_list|)
throws|throws
name|IOException
block|{
name|RegexpQuery
name|fieldCache
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|regexp
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|fieldCache
operator|.
name|setRewriteMethod
argument_list|(
operator|new
name|DocValuesRewriteMethod
argument_list|()
argument_list|)
expr_stmt|;
name|RegexpQuery
name|filter
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|regexp
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|filter
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_FILTER_REWRITE
argument_list|)
expr_stmt|;
name|TopDocs
name|fieldCacheDocs
init|=
name|searcher1
operator|.
name|search
argument_list|(
name|fieldCache
argument_list|,
literal|25
argument_list|)
decl_stmt|;
name|TopDocs
name|filterDocs
init|=
name|searcher2
operator|.
name|search
argument_list|(
name|filter
argument_list|,
literal|25
argument_list|)
decl_stmt|;
name|CheckHits
operator|.
name|checkEqual
argument_list|(
name|fieldCache
argument_list|,
name|fieldCacheDocs
operator|.
name|scoreDocs
argument_list|,
name|filterDocs
operator|.
name|scoreDocs
argument_list|)
expr_stmt|;
block|}
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|RegexpQuery
name|a1
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"[aA]"
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|RegexpQuery
name|a2
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"[aA]"
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|RegexpQuery
name|b
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"[bB]"
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|a1
argument_list|,
name|a2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a1
operator|.
name|equals
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|a1
operator|.
name|setRewriteMethod
argument_list|(
operator|new
name|DocValuesRewriteMethod
argument_list|()
argument_list|)
expr_stmt|;
name|a2
operator|.
name|setRewriteMethod
argument_list|(
operator|new
name|DocValuesRewriteMethod
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|setRewriteMethod
argument_list|(
operator|new
name|DocValuesRewriteMethod
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a1
argument_list|,
name|a2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a1
operator|.
name|equals
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|a1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

