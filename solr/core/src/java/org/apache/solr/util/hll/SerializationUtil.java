begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util.hll
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|hll
package|;
end_package

begin_comment
comment|/**  * A collection of constants and utilities for serializing and deserializing  * HLLs.  *  * NOTE:  'package' visibility is used for many methods that only need to be  *        used by the {@link ISchemaVersion} implementations. The structure of  *        a serialized HLL's metadata should be opaque to the rest of the  *        library.  */
end_comment

begin_class
DECL|class|SerializationUtil
class|class
name|SerializationUtil
block|{
comment|/**      * The number of bits (of the parameters byte) dedicated to encoding the      * width of the registers.      */
DECL|field|REGISTER_WIDTH_BITS
comment|/*package*/
specifier|static
name|int
name|REGISTER_WIDTH_BITS
init|=
literal|3
decl_stmt|;
comment|/**      * A mask to cap the maximum value of the register width.      */
DECL|field|REGISTER_WIDTH_MASK
comment|/*package*/
specifier|static
name|int
name|REGISTER_WIDTH_MASK
init|=
operator|(
literal|1
operator|<<
name|REGISTER_WIDTH_BITS
operator|)
operator|-
literal|1
decl_stmt|;
comment|/**      * The number of bits (of the parameters byte) dedicated to encoding      *<code>log2(registerCount)</code>.      */
DECL|field|LOG2_REGISTER_COUNT_BITS
comment|/*package*/
specifier|static
name|int
name|LOG2_REGISTER_COUNT_BITS
init|=
literal|5
decl_stmt|;
comment|/**      * A mask to cap the maximum value of<code>log2(registerCount)</code>.      */
DECL|field|LOG2_REGISTER_COUNT_MASK
comment|/*package*/
specifier|static
name|int
name|LOG2_REGISTER_COUNT_MASK
init|=
operator|(
literal|1
operator|<<
name|LOG2_REGISTER_COUNT_BITS
operator|)
operator|-
literal|1
decl_stmt|;
comment|/**      * The number of bits (of the cutoff byte) dedicated to encoding the      * log-base-2 of the explicit cutoff or sentinel values for      * 'explicit-disabled' or 'auto'.      */
DECL|field|EXPLICIT_CUTOFF_BITS
comment|/*package*/
specifier|static
name|int
name|EXPLICIT_CUTOFF_BITS
init|=
literal|6
decl_stmt|;
comment|/**      * A mask to cap the maximum value of the explicit cutoff choice.      */
DECL|field|EXPLICIT_CUTOFF_MASK
comment|/*package*/
specifier|static
name|int
name|EXPLICIT_CUTOFF_MASK
init|=
operator|(
literal|1
operator|<<
name|EXPLICIT_CUTOFF_BITS
operator|)
operator|-
literal|1
decl_stmt|;
comment|/**      * Number of bits in a nibble.      */
DECL|field|NIBBLE_BITS
specifier|private
specifier|static
name|int
name|NIBBLE_BITS
init|=
literal|4
decl_stmt|;
comment|/**      * A mask to cap the maximum value of a nibble.      */
DECL|field|NIBBLE_MASK
specifier|private
specifier|static
name|int
name|NIBBLE_MASK
init|=
operator|(
literal|1
operator|<<
name|NIBBLE_BITS
operator|)
operator|-
literal|1
decl_stmt|;
comment|// ************************************************************************
comment|// Serialization utilities
comment|/**      * Schema version one (v1).      */
DECL|field|VERSION_ONE
specifier|public
specifier|static
name|ISchemaVersion
name|VERSION_ONE
init|=
operator|new
name|SchemaVersionOne
argument_list|()
decl_stmt|;
comment|/**      * The default schema version for serializing HLLs.      */
DECL|field|DEFAULT_SCHEMA_VERSION
specifier|public
specifier|static
name|ISchemaVersion
name|DEFAULT_SCHEMA_VERSION
init|=
name|VERSION_ONE
decl_stmt|;
comment|/**      * List of registered schema versions, indexed by their version numbers. If      * an entry is<code>null</code>, then no such schema version is registered.      * Similarly, registering a new schema version simply entails assigning an      * {@link ISchemaVersion} instance to the appropriate index of this array.<p/>      *      * By default, only {@link SchemaVersionOne} is registered. Note that version      * zero will always be reserved for internal (e.g. proprietary, legacy) schema      * specifications/implementations and will never be assigned to in by this      * library.      */
DECL|field|REGISTERED_SCHEMA_VERSIONS
specifier|public
specifier|static
name|ISchemaVersion
index|[]
name|REGISTERED_SCHEMA_VERSIONS
init|=
operator|new
name|ISchemaVersion
index|[
literal|16
index|]
decl_stmt|;
static|static
block|{
name|REGISTERED_SCHEMA_VERSIONS
index|[
literal|1
index|]
operator|=
name|VERSION_ONE
expr_stmt|;
block|}
comment|/**      * @param  schemaVersionNumber the version number of the {@link ISchemaVersion}      *         desired. This must be a registered schema version number.      * @return The {@link ISchemaVersion} for the given number. This will never      *         be<code>null</code>.      */
DECL|method|getSchemaVersion
specifier|public
specifier|static
name|ISchemaVersion
name|getSchemaVersion
parameter_list|(
specifier|final
name|int
name|schemaVersionNumber
parameter_list|)
block|{
if|if
condition|(
name|schemaVersionNumber
operator|>=
name|REGISTERED_SCHEMA_VERSIONS
operator|.
name|length
operator|||
name|schemaVersionNumber
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid schema version number "
operator|+
name|schemaVersionNumber
argument_list|)
throw|;
block|}
specifier|final
name|ISchemaVersion
name|schemaVersion
init|=
name|REGISTERED_SCHEMA_VERSIONS
index|[
name|schemaVersionNumber
index|]
decl_stmt|;
if|if
condition|(
name|schemaVersion
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown schema version number "
operator|+
name|schemaVersionNumber
argument_list|)
throw|;
block|}
return|return
name|schemaVersion
return|;
block|}
comment|/**      * Get the appropriate {@link ISchemaVersion schema version} for the specified      * serialized HLL.      *      * @param  bytes the serialized HLL whose schema version is desired.      * @return the schema version for the specified HLL. This will never      *         be<code>null</code>.      */
DECL|method|getSchemaVersion
specifier|public
specifier|static
name|ISchemaVersion
name|getSchemaVersion
parameter_list|(
specifier|final
name|byte
index|[]
name|bytes
parameter_list|)
block|{
specifier|final
name|byte
name|versionByte
init|=
name|bytes
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|int
name|schemaVersionNumber
init|=
name|schemaVersion
argument_list|(
name|versionByte
argument_list|)
decl_stmt|;
return|return
name|getSchemaVersion
argument_list|(
name|schemaVersionNumber
argument_list|)
return|;
block|}
comment|// ************************************************************************
comment|// Package-specific shared helpers
comment|/**      * Generates a byte that encodes the schema version and the type ordinal      * of the HLL.      *      * The top nibble is the schema version and the bottom nibble is the type      * ordinal.      *      * @param schemaVersion the schema version to encode.      * @param typeOrdinal the type ordinal of the HLL to encode.      * @return the packed version byte      */
DECL|method|packVersionByte
specifier|public
specifier|static
name|byte
name|packVersionByte
parameter_list|(
specifier|final
name|int
name|schemaVersion
parameter_list|,
specifier|final
name|int
name|typeOrdinal
parameter_list|)
block|{
return|return
call|(
name|byte
call|)
argument_list|(
operator|(
operator|(
name|NIBBLE_MASK
operator|&
name|schemaVersion
operator|)
operator|<<
name|NIBBLE_BITS
operator|)
operator||
operator|(
name|NIBBLE_MASK
operator|&
name|typeOrdinal
operator|)
argument_list|)
return|;
block|}
comment|/**      * Generates a byte that encodes the log-base-2 of the explicit cutoff      * or sentinel values for 'explicit-disabled' or 'auto', as well as the      * boolean indicating whether to use {@link HLLType#SPARSE}      * in the promotion hierarchy.      *      * The top bit is always padding, the second highest bit indicates the      * 'sparse-enabled' boolean, and the lowest six bits encode the explicit      * cutoff value.      *      * @param  explicitCutoff the explicit cutoff value to encode.      *<ul>      *<li>      *             If 'explicit-disabled' is chosen, this value should be<code>0</code>.      *</li>      *<li>      *             If 'auto' is chosen, this value should be<code>63</code>.      *</li>      *<li>      *             If a cutoff of 2<sup>n</sup> is desired, for<code>0<= n< 31</code>,      *             this value should be<code>n + 1</code>.      *</li>      *</ul>      * @param  sparseEnabled whether {@link HLLType#SPARSE}      *         should be used in the promotion hierarchy to improve HLL      *         storage.      *      * @return the packed cutoff byte      */
DECL|method|packCutoffByte
specifier|public
specifier|static
name|byte
name|packCutoffByte
parameter_list|(
specifier|final
name|int
name|explicitCutoff
parameter_list|,
specifier|final
name|boolean
name|sparseEnabled
parameter_list|)
block|{
specifier|final
name|int
name|sparseBit
init|=
operator|(
name|sparseEnabled
condition|?
operator|(
literal|1
operator|<<
name|EXPLICIT_CUTOFF_BITS
operator|)
else|:
literal|0
operator|)
decl_stmt|;
return|return
call|(
name|byte
call|)
argument_list|(
name|sparseBit
operator||
operator|(
name|EXPLICIT_CUTOFF_MASK
operator|&
name|explicitCutoff
operator|)
argument_list|)
return|;
block|}
comment|/**      * Generates a byte that encodes the parameters of a      * {@link HLLType#FULL} or {@link HLLType#SPARSE}      * HLL.<p/>      *      * The top 3 bits are used to encode<code>registerWidth - 1</code>      * (range of<code>registerWidth</code> is thus 1-9) and the bottom 5      * bits are used to encode<code>registerCountLog2</code>      * (range of<code>registerCountLog2</code> is thus 0-31).      *      * @param  registerWidth the register width (must be at least 1 and at      *         most 9)      * @param  registerCountLog2 the log-base-2 of the register count (must      *         be at least 0 and at most 31)      * @return the packed parameters byte      */
DECL|method|packParametersByte
specifier|public
specifier|static
name|byte
name|packParametersByte
parameter_list|(
specifier|final
name|int
name|registerWidth
parameter_list|,
specifier|final
name|int
name|registerCountLog2
parameter_list|)
block|{
specifier|final
name|int
name|widthBits
init|=
operator|(
operator|(
name|registerWidth
operator|-
literal|1
operator|)
operator|&
name|REGISTER_WIDTH_MASK
operator|)
decl_stmt|;
specifier|final
name|int
name|countBits
init|=
operator|(
name|registerCountLog2
operator|&
name|LOG2_REGISTER_COUNT_MASK
operator|)
decl_stmt|;
return|return
call|(
name|byte
call|)
argument_list|(
operator|(
name|widthBits
operator|<<
name|LOG2_REGISTER_COUNT_BITS
operator|)
operator||
name|countBits
argument_list|)
return|;
block|}
comment|/**      * Extracts the 'sparse-enabled' boolean from the cutoff byte of a serialized      * HLL.      *      * @param  cutoffByte the cutoff byte of the serialized HLL      * @return the 'sparse-enabled' boolean      */
DECL|method|sparseEnabled
specifier|public
specifier|static
name|boolean
name|sparseEnabled
parameter_list|(
specifier|final
name|byte
name|cutoffByte
parameter_list|)
block|{
return|return
operator|(
operator|(
name|cutoffByte
operator|>>>
name|EXPLICIT_CUTOFF_BITS
operator|)
operator|&
literal|1
operator|)
operator|==
literal|1
return|;
block|}
comment|/**      * Extracts the explicit cutoff value from the cutoff byte of a serialized      * HLL.      *      * @param  cutoffByte the cutoff byte of the serialized HLL      * @return the explicit cutoff value      */
DECL|method|explicitCutoff
specifier|public
specifier|static
name|int
name|explicitCutoff
parameter_list|(
specifier|final
name|byte
name|cutoffByte
parameter_list|)
block|{
return|return
operator|(
name|cutoffByte
operator|&
name|EXPLICIT_CUTOFF_MASK
operator|)
return|;
block|}
comment|/**      * Extracts the schema version from the version byte of a serialized      * HLL.      *      * @param  versionByte the version byte of the serialized HLL      * @return the schema version of the serialized HLL      */
DECL|method|schemaVersion
specifier|public
specifier|static
name|int
name|schemaVersion
parameter_list|(
specifier|final
name|byte
name|versionByte
parameter_list|)
block|{
return|return
name|NIBBLE_MASK
operator|&
operator|(
name|versionByte
operator|>>>
name|NIBBLE_BITS
operator|)
return|;
block|}
comment|/**      * Extracts the type ordinal from the version byte of a serialized HLL.      *      * @param  versionByte the version byte of the serialized HLL      * @return the type ordinal of the serialized HLL      */
DECL|method|typeOrdinal
specifier|public
specifier|static
name|int
name|typeOrdinal
parameter_list|(
specifier|final
name|byte
name|versionByte
parameter_list|)
block|{
return|return
operator|(
name|versionByte
operator|&
name|NIBBLE_MASK
operator|)
return|;
block|}
comment|/**      * Extracts the register width from the parameters byte of a serialized      * {@link HLLType#FULL} HLL.      *      * @param  parametersByte the parameters byte of the serialized HLL      * @return the register width of the serialized HLL      *      * @see #packParametersByte(int, int)      */
DECL|method|registerWidth
specifier|public
specifier|static
name|int
name|registerWidth
parameter_list|(
specifier|final
name|byte
name|parametersByte
parameter_list|)
block|{
return|return
operator|(
operator|(
name|parametersByte
operator|>>>
name|LOG2_REGISTER_COUNT_BITS
operator|)
operator|&
name|REGISTER_WIDTH_MASK
operator|)
operator|+
literal|1
return|;
block|}
comment|/**      * Extracts the log2(registerCount) from the parameters byte of a      * serialized {@link HLLType#FULL} HLL.      *      * @param  parametersByte the parameters byte of the serialized HLL      * @return log2(registerCount) of the serialized HLL      *      * @see #packParametersByte(int, int)      */
DECL|method|registerCountLog2
specifier|public
specifier|static
name|int
name|registerCountLog2
parameter_list|(
specifier|final
name|byte
name|parametersByte
parameter_list|)
block|{
return|return
operator|(
name|parametersByte
operator|&
name|LOG2_REGISTER_COUNT_MASK
operator|)
return|;
block|}
block|}
end_class

end_unit

