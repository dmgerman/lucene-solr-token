begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A test for the {@link DocumentAnalysisResponse} class.  *  *  * @since solr 1.4  */
end_comment

begin_class
DECL|class|DocumentAnalysisResponseTest
specifier|public
class|class
name|DocumentAnalysisResponseTest
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Tests the {@link DocumentAnalysisResponse#setResponse(org.apache.solr.common.util.NamedList)} method    */
annotation|@
name|Test
DECL|method|testSetResponse
specifier|public
name|void
name|testSetResponse
parameter_list|()
throws|throws
name|Exception
block|{
comment|// the parsing of the analysis phases is already tested in the AnalysisResponseBaseTest. So we can just fake
comment|// the phases list here and use it.
specifier|final
name|List
argument_list|<
name|AnalysisResponseBase
operator|.
name|AnalysisPhase
argument_list|>
name|phases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|AnalysisResponseBase
operator|.
name|AnalysisPhase
name|expectedPhase
init|=
operator|new
name|AnalysisResponseBase
operator|.
name|AnalysisPhase
argument_list|(
literal|"Tokenizer"
argument_list|)
decl_stmt|;
name|phases
operator|.
name|add
argument_list|(
name|expectedPhase
argument_list|)
expr_stmt|;
name|NamedList
name|responseNL
init|=
name|buildResponse
argument_list|()
decl_stmt|;
name|DocumentAnalysisResponse
name|response
init|=
operator|new
name|DocumentAnalysisResponse
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|AnalysisPhase
argument_list|>
name|buildPhases
parameter_list|(
name|NamedList
argument_list|<
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|phaseNL
parameter_list|)
block|{
return|return
name|phases
return|;
block|}
block|}
decl_stmt|;
name|response
operator|.
name|setResponse
argument_list|(
name|responseNL
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|response
operator|.
name|getDocumentAnalysesCount
argument_list|()
argument_list|)
expr_stmt|;
name|DocumentAnalysisResponse
operator|.
name|DocumentAnalysis
name|documentAnalysis
init|=
name|response
operator|.
name|getDocumentAnalysis
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|documentAnalysis
operator|.
name|getDocumentKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|documentAnalysis
operator|.
name|getFieldAnalysesCount
argument_list|()
argument_list|)
expr_stmt|;
name|DocumentAnalysisResponse
operator|.
name|FieldAnalysis
name|fieldAnalysis
init|=
name|documentAnalysis
operator|.
name|getFieldAnalysis
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"id"
argument_list|,
name|fieldAnalysis
operator|.
name|getFieldName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fieldAnalysis
operator|.
name|getQueryPhasesCount
argument_list|()
argument_list|)
expr_stmt|;
name|AnalysisResponseBase
operator|.
name|AnalysisPhase
name|phase
init|=
name|fieldAnalysis
operator|.
name|getQueryPhases
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|expectedPhase
argument_list|,
name|phase
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fieldAnalysis
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fieldAnalysis
operator|.
name|getIndexPhasesCount
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|phase
operator|=
name|fieldAnalysis
operator|.
name|getIndexPhases
argument_list|(
literal|"1"
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
name|expectedPhase
argument_list|,
name|phase
argument_list|)
expr_stmt|;
name|fieldAnalysis
operator|=
name|documentAnalysis
operator|.
name|getFieldAnalysis
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"name"
argument_list|,
name|fieldAnalysis
operator|.
name|getFieldName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fieldAnalysis
operator|.
name|getQueryPhasesCount
argument_list|()
argument_list|)
expr_stmt|;
name|phase
operator|=
name|fieldAnalysis
operator|.
name|getQueryPhases
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
name|expectedPhase
argument_list|,
name|phase
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fieldAnalysis
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fieldAnalysis
operator|.
name|getIndexPhasesCount
argument_list|(
literal|"name value 1"
argument_list|)
argument_list|)
expr_stmt|;
name|phase
operator|=
name|fieldAnalysis
operator|.
name|getIndexPhases
argument_list|(
literal|"name value 1"
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
name|expectedPhase
argument_list|,
name|phase
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fieldAnalysis
operator|.
name|getIndexPhasesCount
argument_list|(
literal|"name value 2"
argument_list|)
argument_list|)
expr_stmt|;
name|phase
operator|=
name|fieldAnalysis
operator|.
name|getIndexPhases
argument_list|(
literal|"name value 2"
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
name|expectedPhase
argument_list|,
name|phase
argument_list|)
expr_stmt|;
name|fieldAnalysis
operator|=
name|documentAnalysis
operator|.
name|getFieldAnalysis
argument_list|(
literal|"text"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text"
argument_list|,
name|fieldAnalysis
operator|.
name|getFieldName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fieldAnalysis
operator|.
name|getQueryPhasesCount
argument_list|()
argument_list|)
expr_stmt|;
name|phase
operator|=
name|fieldAnalysis
operator|.
name|getQueryPhases
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
name|expectedPhase
argument_list|,
name|phase
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fieldAnalysis
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fieldAnalysis
operator|.
name|getIndexPhasesCount
argument_list|(
literal|"text value"
argument_list|)
argument_list|)
expr_stmt|;
name|phase
operator|=
name|fieldAnalysis
operator|.
name|getIndexPhases
argument_list|(
literal|"text value"
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
name|expectedPhase
argument_list|,
name|phase
argument_list|)
expr_stmt|;
block|}
comment|//================================================ Helper Methods ==================================================
DECL|method|buildResponse
specifier|private
name|NamedList
name|buildResponse
parameter_list|()
block|{
name|NamedList
name|response
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|NamedList
name|responseHeader
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|response
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
name|responseHeader
argument_list|)
expr_stmt|;
name|NamedList
name|params
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|responseHeader
operator|.
name|add
argument_list|(
literal|"params"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"analysis.showmatch"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"analysis.query"
argument_list|,
literal|"the query"
argument_list|)
expr_stmt|;
name|responseHeader
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|responseHeader
operator|.
name|add
argument_list|(
literal|"QTime"
argument_list|,
literal|105
argument_list|)
expr_stmt|;
name|NamedList
name|analysis
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|response
operator|.
name|add
argument_list|(
literal|"analysis"
argument_list|,
name|analysis
argument_list|)
expr_stmt|;
name|NamedList
name|doc1
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|analysis
operator|.
name|add
argument_list|(
literal|"1"
argument_list|,
name|doc1
argument_list|)
expr_stmt|;
name|NamedList
name|id
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|doc1
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|NamedList
name|query
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|id
operator|.
name|add
argument_list|(
literal|"query"
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|NamedList
name|index
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|id
operator|.
name|add
argument_list|(
literal|"index"
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|NamedList
name|idValue
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|index
operator|.
name|add
argument_list|(
literal|"1"
argument_list|,
name|idValue
argument_list|)
expr_stmt|;
name|NamedList
name|name
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|doc1
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|NamedList
argument_list|()
expr_stmt|;
name|name
operator|.
name|add
argument_list|(
literal|"query"
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|index
operator|=
operator|new
name|NamedList
argument_list|()
expr_stmt|;
name|name
operator|.
name|add
argument_list|(
literal|"index"
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|NamedList
name|nameValue1
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|index
operator|.
name|add
argument_list|(
literal|"name value 1"
argument_list|,
name|nameValue1
argument_list|)
expr_stmt|;
name|NamedList
name|nameValue2
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|index
operator|.
name|add
argument_list|(
literal|"name value 2"
argument_list|,
name|nameValue2
argument_list|)
expr_stmt|;
name|NamedList
name|text
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|doc1
operator|.
name|add
argument_list|(
literal|"text"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|NamedList
argument_list|()
expr_stmt|;
name|text
operator|.
name|add
argument_list|(
literal|"query"
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|index
operator|=
operator|new
name|NamedList
argument_list|()
expr_stmt|;
name|text
operator|.
name|add
argument_list|(
literal|"index"
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|NamedList
name|textValue
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|index
operator|.
name|add
argument_list|(
literal|"text value"
argument_list|,
name|textValue
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
block|}
end_class

end_unit

