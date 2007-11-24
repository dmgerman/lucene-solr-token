begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

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
name|ResourceLoader
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
name|util
operator|.
name|plugin
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
name|StopFilter
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
name|TokenStream
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
name|TokenFilter
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
name|Token
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
name|Set
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

begin_comment
comment|/**  * @version $Id$  */
end_comment

begin_class
DECL|class|EnglishPorterFilterFactory
specifier|public
class|class
name|EnglishPorterFilterFactory
extends|extends
name|BaseTokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
block|{
name|String
name|wordFile
init|=
name|args
operator|.
name|get
argument_list|(
literal|"protected"
argument_list|)
decl_stmt|;
if|if
condition|(
name|wordFile
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|wlist
init|=
name|loader
operator|.
name|getLines
argument_list|(
name|wordFile
argument_list|)
decl_stmt|;
name|protectedWords
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
operator|(
name|String
index|[]
operator|)
name|wlist
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
block|}
block|}
DECL|field|protectedWords
specifier|private
name|Set
name|protectedWords
init|=
literal|null
decl_stmt|;
DECL|method|create
specifier|public
name|EnglishPorterFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|EnglishPorterFilter
argument_list|(
name|input
argument_list|,
name|protectedWords
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|/** English Porter2 filter that doesn't use reflection to /*  adapt lucene to the snowball stemmer code.  */
end_comment

begin_class
DECL|class|EnglishPorterFilter
class|class
name|EnglishPorterFilter
extends|extends
name|TokenFilter
block|{
DECL|field|protWords
specifier|private
specifier|final
name|Set
name|protWords
decl_stmt|;
DECL|field|stemmer
specifier|private
name|net
operator|.
name|sf
operator|.
name|snowball
operator|.
name|ext
operator|.
name|EnglishStemmer
name|stemmer
decl_stmt|;
DECL|method|EnglishPorterFilter
specifier|public
name|EnglishPorterFilter
parameter_list|(
name|TokenStream
name|source
parameter_list|,
name|Set
name|protWords
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|this
operator|.
name|protWords
operator|=
name|protWords
expr_stmt|;
name|stemmer
operator|=
operator|new
name|net
operator|.
name|sf
operator|.
name|snowball
operator|.
name|ext
operator|.
name|EnglishStemmer
argument_list|()
expr_stmt|;
block|}
comment|/** the original code from lucene sandbox   public final Token next() throws IOException {     Token token = input.next();     if (token == null)       return null;     stemmer.setCurrent(token.termText());     try {       stemMethod.invoke(stemmer, EMPTY_ARGS);     } catch (Exception e) {       throw new RuntimeException(e.toString());     }     return new Token(stemmer.getCurrent(),                      token.startOffset(), token.endOffset(), token.type());   }   **/
annotation|@
name|Override
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|Token
name|tok
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|tok
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
name|tokstr
init|=
name|tok
operator|.
name|termText
argument_list|()
decl_stmt|;
comment|// if protected, don't stem.  use this to avoid stemming collisions.
if|if
condition|(
name|protWords
operator|!=
literal|null
operator|&&
name|protWords
operator|.
name|contains
argument_list|(
name|tokstr
argument_list|)
condition|)
block|{
return|return
name|tok
return|;
block|}
name|stemmer
operator|.
name|setCurrent
argument_list|(
name|tokstr
argument_list|)
expr_stmt|;
name|stemmer
operator|.
name|stem
argument_list|()
expr_stmt|;
name|String
name|newstr
init|=
name|stemmer
operator|.
name|getCurrent
argument_list|()
decl_stmt|;
if|if
condition|(
name|tokstr
operator|.
name|equals
argument_list|(
name|newstr
argument_list|)
condition|)
block|{
return|return
name|tok
return|;
block|}
else|else
block|{
comment|// TODO: it would be nice if I could just set termText directly like
comment|// lucene packages can.
name|Token
name|newtok
init|=
operator|new
name|Token
argument_list|(
name|newstr
argument_list|,
name|tok
operator|.
name|startOffset
argument_list|()
argument_list|,
name|tok
operator|.
name|endOffset
argument_list|()
argument_list|,
name|tok
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
name|newtok
operator|.
name|setPositionIncrement
argument_list|(
name|tok
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|newtok
return|;
block|}
block|}
block|}
end_class

end_unit

