begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.quality.trec
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
name|trec
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

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
name|io
operator|.
name|PrintWriter
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|Judge
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

begin_comment
comment|/**  * Judge if given document is relevant to given quality query, based on Trec format for judgements.  */
end_comment

begin_class
DECL|class|TrecJudge
specifier|public
class|class
name|TrecJudge
implements|implements
name|Judge
block|{
DECL|field|judgements
name|HashMap
argument_list|<
name|String
argument_list|,
name|QRelJudgement
argument_list|>
name|judgements
decl_stmt|;
comment|/**    * Constructor from a reader.    *<p>    * Expected input format:    *<pre>    *     qnum  0   doc-name     is-relevant    *</pre>     * Two sample lines:    *<pre>     *     19    0   doc303       1    *     19    0   doc7295      0    *</pre>     * @param reader where judgments are read from.    * @throws IOException If there is a low-level I/O error.    */
DECL|method|TrecJudge
specifier|public
name|TrecJudge
parameter_list|(
name|BufferedReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|judgements
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|QRelJudgement
argument_list|>
argument_list|()
expr_stmt|;
name|QRelJudgement
name|curr
init|=
literal|null
decl_stmt|;
name|String
name|zero
init|=
literal|"0"
decl_stmt|;
name|String
name|line
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|null
operator|!=
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
literal|'#'
operator|==
name|line
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|String
name|queryID
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|st
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|String
name|docName
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|boolean
name|relevant
init|=
operator|!
name|zero
operator|.
name|equals
argument_list|(
name|st
operator|.
name|nextToken
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
operator|!
name|st
operator|.
name|hasMoreTokens
argument_list|()
operator|:
literal|"wrong format: "
operator|+
name|line
operator|+
literal|"  next: "
operator|+
name|st
operator|.
name|nextToken
argument_list|()
assert|;
if|if
condition|(
name|relevant
condition|)
block|{
comment|// only keep relevant docs
if|if
condition|(
name|curr
operator|==
literal|null
operator|||
operator|!
name|curr
operator|.
name|queryID
operator|.
name|equals
argument_list|(
name|queryID
argument_list|)
condition|)
block|{
name|curr
operator|=
name|judgements
operator|.
name|get
argument_list|(
name|queryID
argument_list|)
expr_stmt|;
if|if
condition|(
name|curr
operator|==
literal|null
condition|)
block|{
name|curr
operator|=
operator|new
name|QRelJudgement
argument_list|(
name|queryID
argument_list|)
expr_stmt|;
name|judgements
operator|.
name|put
argument_list|(
name|queryID
argument_list|,
name|curr
argument_list|)
expr_stmt|;
block|}
block|}
name|curr
operator|.
name|addRelevandDoc
argument_list|(
name|docName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// inherit javadocs
annotation|@
name|Override
DECL|method|isRelevant
specifier|public
name|boolean
name|isRelevant
parameter_list|(
name|String
name|docName
parameter_list|,
name|QualityQuery
name|query
parameter_list|)
block|{
name|QRelJudgement
name|qrj
init|=
name|judgements
operator|.
name|get
argument_list|(
name|query
operator|.
name|getQueryID
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|qrj
operator|!=
literal|null
operator|&&
name|qrj
operator|.
name|isRelevant
argument_list|(
name|docName
argument_list|)
return|;
block|}
comment|/** single Judgement of a trec quality query */
DECL|class|QRelJudgement
specifier|private
specifier|static
class|class
name|QRelJudgement
block|{
DECL|field|queryID
specifier|private
name|String
name|queryID
decl_stmt|;
DECL|field|relevantDocs
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|relevantDocs
decl_stmt|;
DECL|method|QRelJudgement
name|QRelJudgement
parameter_list|(
name|String
name|queryID
parameter_list|)
block|{
name|this
operator|.
name|queryID
operator|=
name|queryID
expr_stmt|;
name|relevantDocs
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|addRelevandDoc
specifier|public
name|void
name|addRelevandDoc
parameter_list|(
name|String
name|docName
parameter_list|)
block|{
name|relevantDocs
operator|.
name|put
argument_list|(
name|docName
argument_list|,
name|docName
argument_list|)
expr_stmt|;
block|}
DECL|method|isRelevant
name|boolean
name|isRelevant
parameter_list|(
name|String
name|docName
parameter_list|)
block|{
return|return
name|relevantDocs
operator|.
name|containsKey
argument_list|(
name|docName
argument_list|)
return|;
block|}
DECL|method|maxRecall
specifier|public
name|int
name|maxRecall
parameter_list|()
block|{
return|return
name|relevantDocs
operator|.
name|size
argument_list|()
return|;
block|}
block|}
comment|// inherit javadocs
annotation|@
name|Override
DECL|method|validateData
specifier|public
name|boolean
name|validateData
parameter_list|(
name|QualityQuery
index|[]
name|qq
parameter_list|,
name|PrintWriter
name|logger
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|QRelJudgement
argument_list|>
name|missingQueries
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|QRelJudgement
argument_list|>
argument_list|(
name|judgements
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|missingJudgements
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
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
name|qq
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|qq
index|[
name|i
index|]
operator|.
name|getQueryID
argument_list|()
decl_stmt|;
if|if
condition|(
name|missingQueries
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|missingQueries
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|missingJudgements
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|isValid
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|missingJudgements
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|isValid
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|logger
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|println
argument_list|(
literal|"WARNING: "
operator|+
name|missingJudgements
operator|.
name|size
argument_list|()
operator|+
literal|" queries have no judgments! - "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|missingJudgements
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|logger
operator|.
name|println
argument_list|(
literal|"   "
operator|+
name|missingJudgements
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|missingQueries
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|isValid
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|logger
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|println
argument_list|(
literal|"WARNING: "
operator|+
name|missingQueries
operator|.
name|size
argument_list|()
operator|+
literal|" judgments match no query! - "
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|id
range|:
name|missingQueries
operator|.
name|keySet
argument_list|()
control|)
block|{
name|logger
operator|.
name|println
argument_list|(
literal|"   "
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|isValid
return|;
block|}
comment|// inherit javadocs
annotation|@
name|Override
DECL|method|maxRecall
specifier|public
name|int
name|maxRecall
parameter_list|(
name|QualityQuery
name|query
parameter_list|)
block|{
name|QRelJudgement
name|qrj
init|=
name|judgements
operator|.
name|get
argument_list|(
name|query
operator|.
name|getQueryID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|qrj
operator|!=
literal|null
condition|)
block|{
return|return
name|qrj
operator|.
name|maxRecall
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

