begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|AnalyzerWrapper
import|;
end_import

begin_comment
comment|/**  * This Analyzer limits the number of tokens while indexing. It is  * a replacement for the maximum field length setting inside {@link org.apache.lucene.index.IndexWriter}.  */
end_comment

begin_class
DECL|class|LimitTokenCountAnalyzer
specifier|public
specifier|final
class|class
name|LimitTokenCountAnalyzer
extends|extends
name|AnalyzerWrapper
block|{
DECL|field|delegate
specifier|private
specifier|final
name|Analyzer
name|delegate
decl_stmt|;
DECL|field|maxTokenCount
specifier|private
specifier|final
name|int
name|maxTokenCount
decl_stmt|;
comment|/**    * Build an analyzer that limits the maximum number of tokens per field.    */
DECL|method|LimitTokenCountAnalyzer
specifier|public
name|LimitTokenCountAnalyzer
parameter_list|(
name|Analyzer
name|delegate
parameter_list|,
name|int
name|maxTokenCount
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|maxTokenCount
operator|=
name|maxTokenCount
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWrappedAnalyzer
specifier|protected
name|Analyzer
name|getWrappedAnalyzer
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|delegate
return|;
block|}
annotation|@
name|Override
DECL|method|wrapComponents
specifier|protected
name|TokenStreamComponents
name|wrapComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|TokenStreamComponents
name|components
parameter_list|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|components
operator|.
name|getTokenizer
argument_list|()
argument_list|,
operator|new
name|LimitTokenCountFilter
argument_list|(
name|components
operator|.
name|getTokenStream
argument_list|()
argument_list|,
name|maxTokenCount
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"LimitTokenCountAnalyzer("
operator|+
name|delegate
operator|.
name|toString
argument_list|()
operator|+
literal|", maxTokenCount="
operator|+
name|maxTokenCount
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

