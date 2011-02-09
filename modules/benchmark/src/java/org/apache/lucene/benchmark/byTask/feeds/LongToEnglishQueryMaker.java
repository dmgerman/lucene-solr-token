begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
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
name|Analyzer
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
name|byTask
operator|.
name|tasks
operator|.
name|NewAnalyzerTask
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
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|queryParser
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
name|English
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
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|LongToEnglishQueryMaker
specifier|public
class|class
name|LongToEnglishQueryMaker
implements|implements
name|QueryMaker
block|{
DECL|field|counter
name|long
name|counter
init|=
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|10
decl_stmt|;
DECL|field|parser
specifier|protected
name|QueryParser
name|parser
decl_stmt|;
DECL|method|makeQuery
specifier|public
name|Query
name|makeQuery
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|makeQuery
specifier|public
specifier|synchronized
name|Query
name|makeQuery
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|parser
operator|.
name|parse
argument_list|(
literal|""
operator|+
name|English
operator|.
name|longToEnglish
argument_list|(
name|getNextCounter
argument_list|()
argument_list|)
operator|+
literal|""
argument_list|)
return|;
block|}
DECL|method|getNextCounter
specifier|private
specifier|synchronized
name|long
name|getNextCounter
parameter_list|()
block|{
if|if
condition|(
name|counter
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|counter
operator|=
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|10
expr_stmt|;
block|}
return|return
name|counter
operator|++
return|;
block|}
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|Analyzer
name|anlzr
init|=
name|NewAnalyzerTask
operator|.
name|createAnalyzer
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"analyzer"
argument_list|,
name|StandardAnalyzer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|parser
operator|=
operator|new
name|QueryParser
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|,
name|anlzr
argument_list|)
expr_stmt|;
block|}
DECL|method|resetInputs
specifier|public
name|void
name|resetInputs
parameter_list|()
block|{
name|counter
operator|=
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|10
expr_stmt|;
block|}
DECL|method|printQueries
specifier|public
name|String
name|printQueries
parameter_list|()
block|{
return|return
literal|"LongToEnglish: ["
operator|+
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|" TO "
operator|+
name|counter
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

