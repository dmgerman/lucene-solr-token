begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.uima.ae
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|uima
operator|.
name|ae
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|analysis_engine
operator|.
name|AnalysisEngine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|resource
operator|.
name|ResourceInitializationException
import|;
end_import

begin_comment
comment|/**  * provide an Apache UIMA {@link AnalysisEngine}  *   */
end_comment

begin_interface
DECL|interface|AEProvider
specifier|public
interface|interface
name|AEProvider
block|{
comment|/**    * Returns the AnalysisEngine    */
DECL|method|getAE
specifier|public
name|AnalysisEngine
name|getAE
parameter_list|()
throws|throws
name|ResourceInitializationException
function_decl|;
block|}
end_interface

end_unit

