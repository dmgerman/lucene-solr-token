begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|Reader
import|;
end_import

begin_comment
comment|/**  * Extension to {@link Analyzer} suitable for Analyzers which wrap  * other Analyzers.  *<p/>  * {@link #getWrappedAnalyzer(String)} allows the Analyzer  * to wrap multiple Analyzers which are selected on a per field basis.  *<p/>  * {@link #wrapComponents(String, Analyzer.TokenStreamComponents)} allows the  * TokenStreamComponents of the wrapped Analyzer to then be wrapped  * (such as adding a new {@link TokenFilter} to form new TokenStreamComponents.  */
end_comment

begin_class
DECL|class|AnalyzerWrapper
specifier|public
specifier|abstract
class|class
name|AnalyzerWrapper
extends|extends
name|Analyzer
block|{
comment|/**    * Creates a new AnalyzerWrapper.  Since the {@link Analyzer.ReuseStrategy} of    * the wrapped Analyzers are unknown, {@link #PER_FIELD_REUSE_STRATEGY} is assumed.    * @deprecated Use {@link #AnalyzerWrapper(Analyzer.ReuseStrategy)}    * and specify a valid {@link Analyzer.ReuseStrategy}, probably retrieved from the    * wrapped analyzer using {@link #getReuseStrategy()}.    */
annotation|@
name|Deprecated
DECL|method|AnalyzerWrapper
specifier|protected
name|AnalyzerWrapper
parameter_list|()
block|{
name|this
argument_list|(
name|PER_FIELD_REUSE_STRATEGY
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new AnalyzerWrapper with the given reuse strategy.    *<p>If you want to wrap a single delegate Analyzer you can probably    * reuse its strategy when instantiating this subclass:    * {@code super(delegate.getReuseStrategy());}.    *<p>If you choose different analyzers per field, use    * {@link #PER_FIELD_REUSE_STRATEGY}.    * @see #getReuseStrategy()    */
DECL|method|AnalyzerWrapper
specifier|protected
name|AnalyzerWrapper
parameter_list|(
name|ReuseStrategy
name|reuseStrategy
parameter_list|)
block|{
name|super
argument_list|(
name|reuseStrategy
argument_list|)
expr_stmt|;
block|}
comment|/**    * Retrieves the wrapped Analyzer appropriate for analyzing the field with    * the given name    *    * @param fieldName Name of the field which is to be analyzed    * @return Analyzer for the field with the given name.  Assumed to be non-null    */
DECL|method|getWrappedAnalyzer
specifier|protected
specifier|abstract
name|Analyzer
name|getWrappedAnalyzer
parameter_list|(
name|String
name|fieldName
parameter_list|)
function_decl|;
comment|/**    * Wraps / alters the given TokenStreamComponents, taken from the wrapped    * Analyzer, to form new components. It is through this method that new    * TokenFilters can be added by AnalyzerWrappers. By default, the given    * components are returned.    *     * @param fieldName    *          Name of the field which is to be analyzed    * @param components    *          TokenStreamComponents taken from the wrapped Analyzer    * @return Wrapped / altered TokenStreamComponents.    */
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
name|components
return|;
block|}
comment|/**    * Wraps / alters the given Reader. Through this method AnalyzerWrappers can    * implement {@link #initReader(String, Reader)}. By default, the given reader    * is returned.    *     * @param fieldName    *          name of the field which is to be analyzed    * @param reader    *          the reader to wrap    * @return the wrapped reader    */
DECL|method|wrapReader
specifier|protected
name|Reader
name|wrapReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
name|reader
return|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
specifier|final
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|aReader
parameter_list|)
block|{
return|return
name|wrapComponents
argument_list|(
name|fieldName
argument_list|,
name|getWrappedAnalyzer
argument_list|(
name|fieldName
argument_list|)
operator|.
name|createComponents
argument_list|(
name|fieldName
argument_list|,
name|aReader
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPositionIncrementGap
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|getWrappedAnalyzer
argument_list|(
name|fieldName
argument_list|)
operator|.
name|getPositionIncrementGap
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getOffsetGap
specifier|public
name|int
name|getOffsetGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|getWrappedAnalyzer
argument_list|(
name|fieldName
argument_list|)
operator|.
name|getOffsetGap
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|initReader
specifier|public
specifier|final
name|Reader
name|initReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
name|getWrappedAnalyzer
argument_list|(
name|fieldName
argument_list|)
operator|.
name|initReader
argument_list|(
name|fieldName
argument_list|,
name|wrapReader
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

