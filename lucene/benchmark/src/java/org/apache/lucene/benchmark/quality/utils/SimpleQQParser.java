begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.quality.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|quality
operator|.
name|utils
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|benchmark
operator|.
name|quality
operator|.
name|QualityQuery
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
name|benchmark
operator|.
name|quality
operator|.
name|QualityQueryParser
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
name|queryparser
operator|.
name|classic
operator|.
name|ParseException
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParserBase
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
name|BooleanClause
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
name|BooleanQuery
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
name|Query
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
name|Version
import|;
end_import

begin_comment
comment|/**  * Simplistic quality query parser. A Lucene query is created by passing   * the value of the specified QualityQuery name-value pair(s) into   * a Lucene's QueryParser using StandardAnalyzer. */
end_comment

begin_class
DECL|class|SimpleQQParser
specifier|public
class|class
name|SimpleQQParser
implements|implements
name|QualityQueryParser
block|{
DECL|field|qqNames
specifier|private
name|String
name|qqNames
index|[]
decl_stmt|;
DECL|field|indexField
specifier|private
name|String
name|indexField
decl_stmt|;
DECL|field|queryParser
name|ThreadLocal
argument_list|<
name|QueryParser
argument_list|>
name|queryParser
init|=
operator|new
name|ThreadLocal
argument_list|<
name|QueryParser
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Constructor of a simple qq parser.    * @param qqNames name-value pairs of quality query to use for creating the query    * @param indexField corresponding index field      */
DECL|method|SimpleQQParser
specifier|public
name|SimpleQQParser
parameter_list|(
name|String
name|qqNames
index|[]
parameter_list|,
name|String
name|indexField
parameter_list|)
block|{
name|this
operator|.
name|qqNames
operator|=
name|qqNames
expr_stmt|;
name|this
operator|.
name|indexField
operator|=
name|indexField
expr_stmt|;
block|}
comment|/**    * Constructor of a simple qq parser.    * @param qqName name-value pair of quality query to use for creating the query    * @param indexField corresponding index field      */
DECL|method|SimpleQQParser
specifier|public
name|SimpleQQParser
parameter_list|(
name|String
name|qqName
parameter_list|,
name|String
name|indexField
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|String
index|[]
block|{
name|qqName
block|}
argument_list|,
name|indexField
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.benchmark.quality.QualityQueryParser#parse(org.apache.lucene.benchmark.quality.QualityQuery)    */
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|QualityQuery
name|qq
parameter_list|)
throws|throws
name|ParseException
block|{
name|QueryParser
name|qp
init|=
name|queryParser
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|qp
operator|==
literal|null
condition|)
block|{
name|qp
operator|=
operator|new
name|QueryParser
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|indexField
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|)
expr_stmt|;
name|queryParser
operator|.
name|set
argument_list|(
name|qp
argument_list|)
expr_stmt|;
block|}
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|qqNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|bq
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
name|QueryParserBase
operator|.
name|escape
argument_list|(
name|qq
operator|.
name|getValue
argument_list|(
name|qqNames
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
return|return
name|bq
return|;
block|}
block|}
end_class

end_unit

