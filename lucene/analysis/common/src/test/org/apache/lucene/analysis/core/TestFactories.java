begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
package|;
end_package

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
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|BaseTokenStreamTestCase
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
name|MockTokenizer
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
name|Tokenizer
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
name|miscellaneous
operator|.
name|DelimitedTermFrequencyTokenFilterFactory
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
name|util
operator|.
name|AbstractAnalysisFactory
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
name|util
operator|.
name|CharFilterFactory
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
name|util
operator|.
name|MultiTermAwareComponent
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
name|util
operator|.
name|ResourceLoaderAware
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
name|util
operator|.
name|StringMockResourceLoader
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
name|util
operator|.
name|TokenFilterFactory
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
name|util
operator|.
name|TokenizerFactory
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
name|AttributeFactory
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
comment|/**  * Sanity check some things about all factories,  * we do our best to see if we can sanely initialize it with  * no parameters and smoke test it, etc.  */
end_comment

begin_comment
comment|// TODO: move this, TestRandomChains, and TestAllAnalyzersHaveFactories
end_comment

begin_comment
comment|// to an integration test module that sucks in all analysis modules.
end_comment

begin_comment
comment|// currently the only way to do this is via eclipse etc (LUCENE-3974)
end_comment

begin_comment
comment|// TODO: fix this to use CustomAnalyzer instead of its own FactoryAnalyzer
end_comment

begin_class
DECL|class|TestFactories
specifier|public
class|class
name|TestFactories
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/** Factories that are excluded from testing it with random data */
DECL|field|EXCLUDE_FACTORIES_RANDOM_DATA
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|AbstractAnalysisFactory
argument_list|>
argument_list|>
name|EXCLUDE_FACTORIES_RANDOM_DATA
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|DelimitedTermFrequencyTokenFilterFactory
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|tokenizer
range|:
name|TokenizerFactory
operator|.
name|availableTokenizers
argument_list|()
control|)
block|{
name|doTestTokenizer
argument_list|(
name|tokenizer
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|tokenFilter
range|:
name|TokenFilterFactory
operator|.
name|availableTokenFilters
argument_list|()
control|)
block|{
name|doTestTokenFilter
argument_list|(
name|tokenFilter
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|charFilter
range|:
name|CharFilterFactory
operator|.
name|availableCharFilters
argument_list|()
control|)
block|{
name|doTestCharFilter
argument_list|(
name|charFilter
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doTestTokenizer
specifier|private
name|void
name|doTestTokenizer
parameter_list|(
name|String
name|tokenizer
parameter_list|)
throws|throws
name|IOException
block|{
name|Class
argument_list|<
name|?
extends|extends
name|TokenizerFactory
argument_list|>
name|factoryClazz
init|=
name|TokenizerFactory
operator|.
name|lookupClass
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|TokenizerFactory
name|factory
init|=
operator|(
name|TokenizerFactory
operator|)
name|initialize
argument_list|(
name|factoryClazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
block|{
comment|// we managed to fully create an instance. check a few more things:
comment|// if it implements MultiTermAware, sanity check its impl
if|if
condition|(
name|factory
operator|instanceof
name|MultiTermAwareComponent
condition|)
block|{
name|AbstractAnalysisFactory
name|mtc
init|=
operator|(
operator|(
name|MultiTermAwareComponent
operator|)
name|factory
operator|)
operator|.
name|getMultiTermComponent
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|mtc
argument_list|)
expr_stmt|;
comment|// it's not ok to return e.g. a charfilter here: but a tokenizer could wrap a filter around it
name|assertFalse
argument_list|(
name|mtc
operator|instanceof
name|CharFilterFactory
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|EXCLUDE_FACTORIES_RANDOM_DATA
operator|.
name|contains
argument_list|(
name|factory
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
comment|// beast it just a little, it shouldnt throw exceptions:
comment|// (it should have thrown them in initialize)
name|Analyzer
name|a
init|=
operator|new
name|FactoryAnalyzer
argument_list|(
name|factory
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|20
argument_list|,
literal|20
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|doTestTokenFilter
specifier|private
name|void
name|doTestTokenFilter
parameter_list|(
name|String
name|tokenfilter
parameter_list|)
throws|throws
name|IOException
block|{
name|Class
argument_list|<
name|?
extends|extends
name|TokenFilterFactory
argument_list|>
name|factoryClazz
init|=
name|TokenFilterFactory
operator|.
name|lookupClass
argument_list|(
name|tokenfilter
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|factory
init|=
operator|(
name|TokenFilterFactory
operator|)
name|initialize
argument_list|(
name|factoryClazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
block|{
comment|// we managed to fully create an instance. check a few more things:
comment|// if it implements MultiTermAware, sanity check its impl
if|if
condition|(
name|factory
operator|instanceof
name|MultiTermAwareComponent
condition|)
block|{
name|AbstractAnalysisFactory
name|mtc
init|=
operator|(
operator|(
name|MultiTermAwareComponent
operator|)
name|factory
operator|)
operator|.
name|getMultiTermComponent
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|mtc
argument_list|)
expr_stmt|;
comment|// it's not ok to return a charfilter or tokenizer here, this makes no sense
name|assertTrue
argument_list|(
name|mtc
operator|instanceof
name|TokenFilterFactory
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|EXCLUDE_FACTORIES_RANDOM_DATA
operator|.
name|contains
argument_list|(
name|factory
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
comment|// beast it just a little, it shouldnt throw exceptions:
comment|// (it should have thrown them in initialize)
name|Analyzer
name|a
init|=
operator|new
name|FactoryAnalyzer
argument_list|(
name|assertingTokenizer
argument_list|,
name|factory
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|20
argument_list|,
literal|20
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|doTestCharFilter
specifier|private
name|void
name|doTestCharFilter
parameter_list|(
name|String
name|charfilter
parameter_list|)
throws|throws
name|IOException
block|{
name|Class
argument_list|<
name|?
extends|extends
name|CharFilterFactory
argument_list|>
name|factoryClazz
init|=
name|CharFilterFactory
operator|.
name|lookupClass
argument_list|(
name|charfilter
argument_list|)
decl_stmt|;
name|CharFilterFactory
name|factory
init|=
operator|(
name|CharFilterFactory
operator|)
name|initialize
argument_list|(
name|factoryClazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
block|{
comment|// we managed to fully create an instance. check a few more things:
comment|// if it implements MultiTermAware, sanity check its impl
if|if
condition|(
name|factory
operator|instanceof
name|MultiTermAwareComponent
condition|)
block|{
name|AbstractAnalysisFactory
name|mtc
init|=
operator|(
operator|(
name|MultiTermAwareComponent
operator|)
name|factory
operator|)
operator|.
name|getMultiTermComponent
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|mtc
argument_list|)
expr_stmt|;
comment|// it's not ok to return a tokenizer or tokenfilter here, this makes no sense
name|assertTrue
argument_list|(
name|mtc
operator|instanceof
name|CharFilterFactory
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|EXCLUDE_FACTORIES_RANDOM_DATA
operator|.
name|contains
argument_list|(
name|factory
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
comment|// beast it just a little, it shouldnt throw exceptions:
comment|// (it should have thrown them in initialize)
name|Analyzer
name|a
init|=
operator|new
name|FactoryAnalyzer
argument_list|(
name|assertingTokenizer
argument_list|,
literal|null
argument_list|,
name|factory
argument_list|)
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|20
argument_list|,
literal|20
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** tries to initialize a factory with no arguments */
DECL|method|initialize
specifier|private
name|AbstractAnalysisFactory
name|initialize
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|AbstractAnalysisFactory
argument_list|>
name|factoryClazz
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"luceneMatchVersion"
argument_list|,
name|Version
operator|.
name|LATEST
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Constructor
argument_list|<
name|?
extends|extends
name|AbstractAnalysisFactory
argument_list|>
name|ctor
decl_stmt|;
try|try
block|{
name|ctor
operator|=
name|factoryClazz
operator|.
name|getConstructor
argument_list|(
name|Map
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"factory '"
operator|+
name|factoryClazz
operator|+
literal|"' does not have a proper ctor!"
argument_list|)
throw|;
block|}
name|AbstractAnalysisFactory
name|factory
init|=
literal|null
decl_stmt|;
try|try
block|{
name|factory
operator|=
name|ctor
operator|.
name|newInstance
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
decl||
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IllegalArgumentException
condition|)
block|{
comment|// it's ok if we dont provide the right parameters to throw this
return|return
literal|null
return|;
block|}
block|}
if|if
condition|(
name|factory
operator|instanceof
name|ResourceLoaderAware
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|ResourceLoaderAware
operator|)
name|factory
operator|)
operator|.
name|inform
argument_list|(
operator|new
name|StringMockResourceLoader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{
comment|// it's ok if the right files arent available or whatever to throw this
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ignored
parameter_list|)
block|{
comment|// is this ok? I guess so
block|}
block|}
return|return
name|factory
return|;
block|}
comment|// some silly classes just so we can use checkRandomData
DECL|field|assertingTokenizer
specifier|private
name|TokenizerFactory
name|assertingTokenizer
init|=
operator|new
name|TokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|MockTokenizer
name|create
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|)
block|{
return|return
operator|new
name|MockTokenizer
argument_list|(
name|factory
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|class|FactoryAnalyzer
specifier|private
specifier|static
class|class
name|FactoryAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|tokenizer
specifier|final
name|TokenizerFactory
name|tokenizer
decl_stmt|;
DECL|field|charFilter
specifier|final
name|CharFilterFactory
name|charFilter
decl_stmt|;
DECL|field|tokenfilter
specifier|final
name|TokenFilterFactory
name|tokenfilter
decl_stmt|;
DECL|method|FactoryAnalyzer
name|FactoryAnalyzer
parameter_list|(
name|TokenizerFactory
name|tokenizer
parameter_list|,
name|TokenFilterFactory
name|tokenfilter
parameter_list|,
name|CharFilterFactory
name|charFilter
parameter_list|)
block|{
assert|assert
name|tokenizer
operator|!=
literal|null
assert|;
name|this
operator|.
name|tokenizer
operator|=
name|tokenizer
expr_stmt|;
name|this
operator|.
name|charFilter
operator|=
name|charFilter
expr_stmt|;
name|this
operator|.
name|tokenfilter
operator|=
name|tokenfilter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tf
init|=
name|tokenizer
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenfilter
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tf
argument_list|,
name|tokenfilter
operator|.
name|create
argument_list|(
name|tf
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tf
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|initReader
specifier|protected
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
if|if
condition|(
name|charFilter
operator|!=
literal|null
condition|)
block|{
return|return
name|charFilter
operator|.
name|create
argument_list|(
name|reader
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|reader
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

