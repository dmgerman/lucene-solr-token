begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
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
name|tasks
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
name|FilesystemResourceLoader
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|AnalyzerFactory
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StreamTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|List
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
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Analyzer factory construction task.  The name given to the constructed factory may  * be given to NewAnalyzerTask, which will call AnalyzerFactory.create().  *  * Params are in the form argname:argvalue or argname:"argvalue" or argname:'argvalue';  * use backslashes to escape '"' or "'" inside a quoted value when it's used as the enclosing  * quotation mark,  *  * Specify params in a comma separated list of the following, in order:  *<ol>  *<li>Analyzer args:  *<ul>  *<li><b>Required</b>:<code>name:<i>analyzer-factory-name</i></code></li>  *<li>Optional:<tt>positionIncrementGap:<i>int value</i></tt> (default: 0)</li>  *<li>Optional:<tt>offsetGap:<i>int value</i></tt> (default: 1)</li>  *</ul>  *</li>  *<li>zero or more CharFilterFactory's, followed by</li>  *<li>exactly one TokenizerFactory, followed by</li>  *<li>zero or more TokenFilterFactory's</li>  *</ol>  *  * Each component analysis factory map specify<tt>luceneMatchVersion</tt> (defaults to  * {@link Version#LUCENE_CURRENT}) and any of the args understood by the specified  * *Factory class, in the above-describe param format.  *<p/>  * Example:  *<pre>  *     -AnalyzerFactory(name:'strip html, fold to ascii, whitespace tokenize, max 10k tokens',  *                      positionIncrementGap:100,  *                      HTMLStripCharFilter,  *                      MappingCharFilter(mapping:'mapping-FoldToASCII.txt'),  *                      WhitespaceTokenizer(luceneMatchVersion:LUCENE_42),  *                      TokenLimitFilter(maxTokenCount:10000, consumeAllTokens:false))  *     [...]  *     -NewAnalyzer('strip html, fold to ascii, whitespace tokenize, max 10k tokens')  *</pre>  *<p/>  * AnalyzerFactory will direct analysis component factories to look for resources  * under the directory specified in the "work.dir" property.  */
end_comment

begin_class
DECL|class|AnalyzerFactoryTask
specifier|public
class|class
name|AnalyzerFactoryTask
extends|extends
name|PerfTask
block|{
DECL|field|LUCENE_ANALYSIS_PACKAGE_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|LUCENE_ANALYSIS_PACKAGE_PREFIX
init|=
literal|"org.apache.lucene.analysis."
decl_stmt|;
DECL|field|ANALYSIS_COMPONENT_SUFFIX_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|ANALYSIS_COMPONENT_SUFFIX_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?s:(?:(?:Token|Char)?Filter|Tokenizer)(?:Factory)?)$"
argument_list|)
decl_stmt|;
DECL|field|TRAILING_DOT_ZERO_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|TRAILING_DOT_ZERO_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\.0$"
argument_list|)
decl_stmt|;
DECL|enum|ArgType
DECL|enum constant|ANALYZER_ARG
DECL|enum constant|ANALYZER_ARG_OR_CHARFILTER_OR_TOKENIZER
DECL|enum constant|TOKENFILTER
specifier|private
enum|enum
name|ArgType
block|{
name|ANALYZER_ARG
block|,
name|ANALYZER_ARG_OR_CHARFILTER_OR_TOKENIZER
block|,
name|TOKENFILTER
block|}
DECL|field|factoryName
name|String
name|factoryName
init|=
literal|null
decl_stmt|;
DECL|field|positionIncrementGap
name|Integer
name|positionIncrementGap
init|=
literal|null
decl_stmt|;
DECL|field|offsetGap
name|Integer
name|offsetGap
init|=
literal|null
decl_stmt|;
DECL|field|charFilterFactories
specifier|private
name|List
argument_list|<
name|CharFilterFactory
argument_list|>
name|charFilterFactories
init|=
operator|new
name|ArrayList
argument_list|<
name|CharFilterFactory
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|tokenizerFactory
specifier|private
name|TokenizerFactory
name|tokenizerFactory
init|=
literal|null
decl_stmt|;
DECL|field|tokenFilterFactories
specifier|private
name|List
argument_list|<
name|TokenFilterFactory
argument_list|>
name|tokenFilterFactories
init|=
operator|new
name|ArrayList
argument_list|<
name|TokenFilterFactory
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|AnalyzerFactoryTask
specifier|public
name|AnalyzerFactoryTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
comment|/**    * Sets the params.    * Analysis component factory names may optionally include the "Factory" suffix.    *    * @param params analysis pipeline specification: name, (optional) positionIncrementGap,    *               (optional) offsetGap, 0+ CharFilterFactory's, 1 TokenizerFactory,    *               and 0+ TokenFilterFactory's    */
annotation|@
name|Override
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
name|super
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|ArgType
name|expectedArgType
init|=
name|ArgType
operator|.
name|ANALYZER_ARG
decl_stmt|;
specifier|final
name|StreamTokenizer
name|stok
init|=
operator|new
name|StreamTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|params
argument_list|)
argument_list|)
decl_stmt|;
name|stok
operator|.
name|commentChar
argument_list|(
literal|'#'
argument_list|)
expr_stmt|;
name|stok
operator|.
name|quoteChar
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|stok
operator|.
name|quoteChar
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|stok
operator|.
name|eolIsSignificant
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|stok
operator|.
name|ordinaryChar
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|stok
operator|.
name|ordinaryChar
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
name|stok
operator|.
name|ordinaryChar
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|stok
operator|.
name|ordinaryChar
argument_list|(
literal|','
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
name|stok
operator|.
name|nextToken
argument_list|()
operator|!=
name|StreamTokenizer
operator|.
name|TT_EOF
condition|)
block|{
switch|switch
condition|(
name|stok
operator|.
name|ttype
condition|)
block|{
case|case
literal|','
case|:
block|{
comment|// Do nothing
break|break;
block|}
case|case
name|StreamTokenizer
operator|.
name|TT_WORD
case|:
block|{
if|if
condition|(
name|expectedArgType
operator|.
name|equals
argument_list|(
name|ArgType
operator|.
name|ANALYZER_ARG
argument_list|)
condition|)
block|{
specifier|final
name|String
name|argName
init|=
name|stok
operator|.
name|sval
decl_stmt|;
if|if
condition|(
operator|!
name|argName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"name"
argument_list|)
operator|&&
operator|!
name|argName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"positionIncrementGap"
argument_list|)
operator|&&
operator|!
name|argName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"offsetGap"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Missing 'name' param to AnalyzerFactory: '"
operator|+
name|params
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|stok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|stok
operator|.
name|ttype
operator|!=
literal|':'
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Missing ':' after '"
operator|+
name|argName
operator|+
literal|"' param to AnalyzerFactory"
argument_list|)
throw|;
block|}
name|stok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|String
name|argValue
init|=
name|stok
operator|.
name|sval
decl_stmt|;
switch|switch
condition|(
name|stok
operator|.
name|ttype
condition|)
block|{
case|case
name|StreamTokenizer
operator|.
name|TT_NUMBER
case|:
block|{
name|argValue
operator|=
name|Double
operator|.
name|toString
argument_list|(
name|stok
operator|.
name|nval
argument_list|)
expr_stmt|;
comment|// Drop the ".0" from numbers, for integer arguments
name|argValue
operator|=
name|TRAILING_DOT_ZERO_PATTERN
operator|.
name|matcher
argument_list|(
name|argValue
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|// Intentional fallthrough
block|}
case|case
literal|'"'
case|:
case|case
literal|'\''
case|:
case|case
name|StreamTokenizer
operator|.
name|TT_WORD
case|:
block|{
if|if
condition|(
name|argName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"name"
argument_list|)
condition|)
block|{
name|factoryName
operator|=
name|argValue
expr_stmt|;
name|expectedArgType
operator|=
name|ArgType
operator|.
name|ANALYZER_ARG_OR_CHARFILTER_OR_TOKENIZER
expr_stmt|;
block|}
else|else
block|{
name|int
name|intArgValue
init|=
literal|0
decl_stmt|;
try|try
block|{
name|intArgValue
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|argValue
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Exception parsing "
operator|+
name|argName
operator|+
literal|" value '"
operator|+
name|argValue
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|argName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"positionIncrementGap"
argument_list|)
condition|)
block|{
name|positionIncrementGap
operator|=
name|intArgValue
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|argName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"offsetGap"
argument_list|)
condition|)
block|{
name|offsetGap
operator|=
name|intArgValue
expr_stmt|;
block|}
block|}
break|break;
block|}
case|case
name|StreamTokenizer
operator|.
name|TT_EOF
case|:
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected EOF: "
operator|+
name|stok
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
default|default:
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Unexpected token: "
operator|+
name|stok
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|expectedArgType
operator|.
name|equals
argument_list|(
name|ArgType
operator|.
name|ANALYZER_ARG_OR_CHARFILTER_OR_TOKENIZER
argument_list|)
condition|)
block|{
specifier|final
name|String
name|argName
init|=
name|stok
operator|.
name|sval
decl_stmt|;
if|if
condition|(
name|argName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"positionIncrementGap"
argument_list|)
operator|||
name|argName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"offsetGap"
argument_list|)
condition|)
block|{
name|stok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|stok
operator|.
name|ttype
operator|!=
literal|':'
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Missing ':' after '"
operator|+
name|argName
operator|+
literal|"' param to AnalyzerFactory"
argument_list|)
throw|;
block|}
name|stok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|int
name|intArgValue
init|=
operator|(
name|int
operator|)
name|stok
operator|.
name|nval
decl_stmt|;
switch|switch
condition|(
name|stok
operator|.
name|ttype
condition|)
block|{
case|case
literal|'"'
case|:
case|case
literal|'\''
case|:
case|case
name|StreamTokenizer
operator|.
name|TT_WORD
case|:
block|{
name|intArgValue
operator|=
literal|0
expr_stmt|;
try|try
block|{
name|intArgValue
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|stok
operator|.
name|sval
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Exception parsing "
operator|+
name|argName
operator|+
literal|" value '"
operator|+
name|stok
operator|.
name|sval
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// Intentional fall-through
block|}
case|case
name|StreamTokenizer
operator|.
name|TT_NUMBER
case|:
block|{
if|if
condition|(
name|argName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"positionIncrementGap"
argument_list|)
condition|)
block|{
name|positionIncrementGap
operator|=
name|intArgValue
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|argName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"offsetGap"
argument_list|)
condition|)
block|{
name|offsetGap
operator|=
name|intArgValue
expr_stmt|;
block|}
break|break;
block|}
case|case
name|StreamTokenizer
operator|.
name|TT_EOF
case|:
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected EOF: "
operator|+
name|stok
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
default|default:
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Unexpected token: "
operator|+
name|stok
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
break|break;
block|}
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|CharFilterFactory
argument_list|>
name|clazz
decl_stmt|;
name|clazz
operator|=
name|lookupAnalysisClass
argument_list|(
name|argName
argument_list|,
name|CharFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|createAnalysisPipelineComponent
argument_list|(
name|stok
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|TokenizerFactory
argument_list|>
name|clazz
decl_stmt|;
name|clazz
operator|=
name|lookupAnalysisClass
argument_list|(
name|argName
argument_list|,
name|TokenizerFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|createAnalysisPipelineComponent
argument_list|(
name|stok
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
name|expectedArgType
operator|=
name|ArgType
operator|.
name|TOKENFILTER
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e2
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Can't find class '"
operator|+
name|argName
operator|+
literal|"' as CharFilterFactory or TokenizerFactory"
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
comment|// expectedArgType = ArgType.TOKENFILTER
specifier|final
name|String
name|className
init|=
name|stok
operator|.
name|sval
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|TokenFilterFactory
argument_list|>
name|clazz
decl_stmt|;
try|try
block|{
name|clazz
operator|=
name|lookupAnalysisClass
argument_list|(
name|className
argument_list|,
name|TokenFilterFactory
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Can't find class '"
operator|+
name|className
operator|+
literal|"' as TokenFilterFactory"
argument_list|)
throw|;
block|}
name|createAnalysisPipelineComponent
argument_list|(
name|stok
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
default|default:
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Unexpected token: "
operator|+
name|stok
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Line #"
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": "
argument_list|,
name|t
argument_list|)
throw|;
block|}
specifier|final
name|AnalyzerFactory
name|analyzerFactory
init|=
operator|new
name|AnalyzerFactory
argument_list|(
name|charFilterFactories
argument_list|,
name|tokenizerFactory
argument_list|,
name|tokenFilterFactories
argument_list|)
decl_stmt|;
name|analyzerFactory
operator|.
name|setPositionIncrementGap
argument_list|(
name|positionIncrementGap
argument_list|)
expr_stmt|;
name|analyzerFactory
operator|.
name|setOffsetGap
argument_list|(
name|offsetGap
argument_list|)
expr_stmt|;
name|getRunData
argument_list|()
operator|.
name|getAnalyzerFactories
argument_list|()
operator|.
name|put
argument_list|(
name|factoryName
argument_list|,
name|analyzerFactory
argument_list|)
expr_stmt|;
block|}
comment|/**    * Instantiates the given analysis factory class after pulling params from    * the given stream tokenizer, then stores the result in the appropriate    * pipeline component list.    *    * @param stok stream tokenizer from which to draw analysis factory params    * @param clazz analysis factory class to instantiate    */
DECL|method|createAnalysisPipelineComponent
specifier|private
name|void
name|createAnalysisPipelineComponent
parameter_list|(
name|StreamTokenizer
name|stok
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|AbstractAnalysisFactory
argument_list|>
name|clazz
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|argMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|parenthetical
init|=
literal|false
decl_stmt|;
try|try
block|{
name|WHILE_LOOP
label|:
while|while
condition|(
name|stok
operator|.
name|nextToken
argument_list|()
operator|!=
name|StreamTokenizer
operator|.
name|TT_EOF
condition|)
block|{
switch|switch
condition|(
name|stok
operator|.
name|ttype
condition|)
block|{
case|case
literal|','
case|:
block|{
if|if
condition|(
name|parenthetical
condition|)
block|{
comment|// Do nothing
break|break;
block|}
else|else
block|{
comment|// Finished reading this analysis factory configuration
break|break
name|WHILE_LOOP
break|;
block|}
block|}
case|case
literal|'('
case|:
block|{
if|if
condition|(
name|parenthetical
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Unexpected opening parenthesis."
argument_list|)
throw|;
block|}
name|parenthetical
operator|=
literal|true
expr_stmt|;
break|break;
block|}
case|case
literal|')'
case|:
block|{
if|if
condition|(
name|parenthetical
condition|)
block|{
name|parenthetical
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Unexpected closing parenthesis."
argument_list|)
throw|;
block|}
break|break;
block|}
case|case
name|StreamTokenizer
operator|.
name|TT_WORD
case|:
block|{
if|if
condition|(
operator|!
name|parenthetical
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Unexpected token '"
operator|+
name|stok
operator|.
name|sval
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|String
name|argName
init|=
name|stok
operator|.
name|sval
decl_stmt|;
name|stok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|stok
operator|.
name|ttype
operator|!=
literal|':'
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Missing ':' after '"
operator|+
name|argName
operator|+
literal|"' param to "
operator|+
name|clazz
operator|.
name|getSimpleName
argument_list|()
argument_list|)
throw|;
block|}
name|stok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|String
name|argValue
init|=
name|stok
operator|.
name|sval
decl_stmt|;
switch|switch
condition|(
name|stok
operator|.
name|ttype
condition|)
block|{
case|case
name|StreamTokenizer
operator|.
name|TT_NUMBER
case|:
block|{
name|argValue
operator|=
name|Double
operator|.
name|toString
argument_list|(
name|stok
operator|.
name|nval
argument_list|)
expr_stmt|;
comment|// Drop the ".0" from numbers, for integer arguments
name|argValue
operator|=
name|TRAILING_DOT_ZERO_PATTERN
operator|.
name|matcher
argument_list|(
name|argValue
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|// Intentional fall-through
block|}
case|case
literal|'"'
case|:
case|case
literal|'\''
case|:
case|case
name|StreamTokenizer
operator|.
name|TT_WORD
case|:
block|{
name|argMap
operator|.
name|put
argument_list|(
name|argName
argument_list|,
name|argValue
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|StreamTokenizer
operator|.
name|TT_EOF
case|:
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected EOF: "
operator|+
name|stok
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
default|default:
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": Unexpected token: "
operator|+
name|stok
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
operator|!
name|argMap
operator|.
name|containsKey
argument_list|(
literal|"luceneMatchVersion"
argument_list|)
condition|)
block|{
name|argMap
operator|.
name|put
argument_list|(
literal|"luceneMatchVersion"
argument_list|,
name|Version
operator|.
name|LUCENE_CURRENT
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AbstractAnalysisFactory
name|instance
decl_stmt|;
try|try
block|{
name|instance
operator|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|Map
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|(
name|argMap
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
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": "
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|instance
operator|instanceof
name|ResourceLoaderAware
condition|)
block|{
name|File
name|baseDir
init|=
operator|new
name|File
argument_list|(
name|getRunData
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"work.dir"
argument_list|,
literal|"work"
argument_list|)
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|baseDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|baseDir
operator|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
block|}
operator|(
operator|(
name|ResourceLoaderAware
operator|)
name|instance
operator|)
operator|.
name|inform
argument_list|(
operator|new
name|FilesystemResourceLoader
argument_list|(
name|baseDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|CharFilterFactory
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|charFilterFactories
operator|.
name|add
argument_list|(
operator|(
name|CharFilterFactory
operator|)
name|instance
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|TokenizerFactory
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|tokenizerFactory
operator|=
operator|(
name|TokenizerFactory
operator|)
name|instance
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|TokenFilterFactory
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|tokenFilterFactories
operator|.
name|add
argument_list|(
operator|(
name|TokenFilterFactory
operator|)
name|instance
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Line #"
argument_list|)
condition|)
block|{
throw|throw
operator|(
name|e
operator|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Line #"
operator|+
name|lineno
argument_list|(
name|stok
argument_list|)
operator|+
literal|": "
argument_list|,
name|t
argument_list|)
throw|;
block|}
block|}
comment|/**    * This method looks up a class with its fully qualified name (FQN), or a short-name    * class-simplename, or with a package suffix, assuming "org.apache.lucene.analysis."    * as the package prefix (e.g. "standard.ClassicTokenizerFactory" ->    * "org.apache.lucene.analysis.standard.ClassicTokenizerFactory").    *    * If className contains a period, the class is first looked up as-is, assuming that it    * is an FQN.  If this fails, lookup is retried after prepending the Lucene analysis    * package prefix to the class name.    *    * If className does not contain a period, the analysis SPI *Factory.lookupClass()    * methods are used to find the class.    *    * @param className The name or the short name of the class.    * @param expectedType The superclass className is expected to extend    * @return the loaded class.    * @throws ClassNotFoundException if lookup fails    */
DECL|method|lookupAnalysisClass
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|lookupAnalysisClass
parameter_list|(
name|String
name|className
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|expectedType
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
if|if
condition|(
name|className
operator|.
name|contains
argument_list|(
literal|"."
argument_list|)
condition|)
block|{
try|try
block|{
comment|// First, try className == FQN
return|return
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|expectedType
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
try|try
block|{
comment|// Second, retry lookup after prepending the Lucene analysis package prefix
return|return
name|Class
operator|.
name|forName
argument_list|(
name|LUCENE_ANALYSIS_PACKAGE_PREFIX
operator|+
name|className
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|expectedType
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|ClassNotFoundException
argument_list|(
literal|"Can't find class '"
operator|+
name|className
operator|+
literal|"' or '"
operator|+
name|LUCENE_ANALYSIS_PACKAGE_PREFIX
operator|+
name|className
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
block|}
comment|// No dot - use analysis SPI lookup
specifier|final
name|String
name|analysisComponentName
init|=
name|ANALYSIS_COMPONENT_SUFFIX_PATTERN
operator|.
name|matcher
argument_list|(
name|className
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|CharFilterFactory
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|expectedType
argument_list|)
condition|)
block|{
return|return
name|CharFilterFactory
operator|.
name|lookupClass
argument_list|(
name|analysisComponentName
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|expectedType
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|TokenizerFactory
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|expectedType
argument_list|)
condition|)
block|{
return|return
name|TokenizerFactory
operator|.
name|lookupClass
argument_list|(
name|analysisComponentName
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|expectedType
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|TokenFilterFactory
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|expectedType
argument_list|)
condition|)
block|{
return|return
name|TokenFilterFactory
operator|.
name|lookupClass
argument_list|(
name|analysisComponentName
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|expectedType
argument_list|)
return|;
block|}
throw|throw
operator|new
name|ClassNotFoundException
argument_list|(
literal|"Can't find class '"
operator|+
name|className
operator|+
literal|"'"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.tasks.PerfTask#supportsParams()    */
annotation|@
name|Override
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** Returns the current line in the algorithm file */
DECL|method|lineno
specifier|public
name|int
name|lineno
parameter_list|(
name|StreamTokenizer
name|stok
parameter_list|)
block|{
return|return
name|getAlgLineNum
argument_list|()
operator|+
name|stok
operator|.
name|lineno
argument_list|()
return|;
block|}
block|}
end_class

end_unit

