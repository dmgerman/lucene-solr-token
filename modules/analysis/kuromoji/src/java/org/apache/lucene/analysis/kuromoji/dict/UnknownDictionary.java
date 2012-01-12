begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.kuromoji.dict
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|kuromoji
operator|.
name|dict
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|UnknownDictionary
specifier|public
specifier|final
class|class
name|UnknownDictionary
extends|extends
name|BinaryDictionary
block|{
DECL|field|characterDefinition
specifier|private
specifier|final
name|CharacterDefinition
name|characterDefinition
init|=
name|CharacterDefinition
operator|.
name|getInstance
argument_list|()
decl_stmt|;
DECL|method|UnknownDictionary
specifier|private
name|UnknownDictionary
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|lookup
specifier|public
name|int
name|lookup
parameter_list|(
name|char
index|[]
name|text
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
operator|!
name|characterDefinition
operator|.
name|isGroup
argument_list|(
name|text
index|[
name|offset
index|]
argument_list|)
condition|)
block|{
return|return
literal|1
return|;
block|}
comment|// Extract unknown word. Characters with the same character class are considered to be part of unknown word
name|byte
name|characterIdOfFirstCharacter
init|=
name|characterDefinition
operator|.
name|getCharacterClass
argument_list|(
name|text
index|[
name|offset
index|]
argument_list|)
decl_stmt|;
name|int
name|length
init|=
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|characterIdOfFirstCharacter
operator|==
name|characterDefinition
operator|.
name|getCharacterClass
argument_list|(
name|text
index|[
name|offset
operator|+
name|i
index|]
argument_list|)
condition|)
block|{
name|length
operator|++
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
name|length
return|;
block|}
DECL|method|getCharacterDefinition
specifier|public
name|CharacterDefinition
name|getCharacterDefinition
parameter_list|()
block|{
return|return
name|characterDefinition
return|;
block|}
annotation|@
name|Override
DECL|method|getReading
specifier|public
name|String
name|getReading
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getInflectionType
specifier|public
name|String
name|getInflectionType
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getInflectionForm
specifier|public
name|String
name|getInflectionForm
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|getInstance
specifier|public
specifier|static
name|UnknownDictionary
name|getInstance
parameter_list|()
block|{
return|return
name|SingletonHolder
operator|.
name|INSTANCE
return|;
block|}
DECL|class|SingletonHolder
specifier|private
specifier|static
class|class
name|SingletonHolder
block|{
DECL|field|INSTANCE
specifier|static
specifier|final
name|UnknownDictionary
name|INSTANCE
decl_stmt|;
static|static
block|{
try|try
block|{
name|INSTANCE
operator|=
operator|new
name|UnknownDictionary
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot load UnknownDictionary."
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

