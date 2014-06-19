begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|PlatformManagedObject
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormatSymbols
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Estimates the size (memory representation) of Java objects.  *   * @see #shallowSizeOf(Object)  * @see #shallowSizeOfInstance(Class)  *   * @lucene.internal  */
end_comment

begin_class
DECL|class|RamUsageEstimator
specifier|public
specifier|final
class|class
name|RamUsageEstimator
block|{
comment|/**    * JVM diagnostic features.    */
DECL|enum|JvmFeature
specifier|public
specifier|static
enum|enum
name|JvmFeature
block|{
DECL|enum constant|OBJECT_REFERENCE_SIZE
name|OBJECT_REFERENCE_SIZE
argument_list|(
literal|"Object reference size estimated using array index scale"
argument_list|)
block|,
DECL|enum constant|ARRAY_HEADER_SIZE
name|ARRAY_HEADER_SIZE
argument_list|(
literal|"Array header size estimated using array based offset"
argument_list|)
block|,
DECL|enum constant|FIELD_OFFSETS
name|FIELD_OFFSETS
argument_list|(
literal|"Shallow instance size based on field offsets"
argument_list|)
block|,
DECL|enum constant|OBJECT_ALIGNMENT
name|OBJECT_ALIGNMENT
argument_list|(
literal|"Object alignment retrieved from HotSpotDiagnostic MX bean"
argument_list|)
block|;
DECL|field|description
specifier|public
specifier|final
name|String
name|description
decl_stmt|;
DECL|method|JvmFeature
specifier|private
name|JvmFeature
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
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
name|super
operator|.
name|name
argument_list|()
operator|+
literal|" ("
operator|+
name|description
operator|+
literal|")"
return|;
block|}
block|}
comment|/** JVM info string for debugging and reports. */
DECL|field|JVM_INFO_STRING
specifier|public
specifier|final
specifier|static
name|String
name|JVM_INFO_STRING
decl_stmt|;
comment|/** One kilobyte bytes. */
DECL|field|ONE_KB
specifier|public
specifier|static
specifier|final
name|long
name|ONE_KB
init|=
literal|1024
decl_stmt|;
comment|/** One megabyte bytes. */
DECL|field|ONE_MB
specifier|public
specifier|static
specifier|final
name|long
name|ONE_MB
init|=
name|ONE_KB
operator|*
name|ONE_KB
decl_stmt|;
comment|/** One gigabyte bytes.*/
DECL|field|ONE_GB
specifier|public
specifier|static
specifier|final
name|long
name|ONE_GB
init|=
name|ONE_KB
operator|*
name|ONE_MB
decl_stmt|;
comment|/** No instantiation. */
DECL|method|RamUsageEstimator
specifier|private
name|RamUsageEstimator
parameter_list|()
block|{}
DECL|field|NUM_BYTES_BOOLEAN
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_BOOLEAN
init|=
literal|1
decl_stmt|;
DECL|field|NUM_BYTES_BYTE
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_BYTE
init|=
literal|1
decl_stmt|;
DECL|field|NUM_BYTES_CHAR
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_CHAR
init|=
literal|2
decl_stmt|;
DECL|field|NUM_BYTES_SHORT
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_SHORT
init|=
literal|2
decl_stmt|;
DECL|field|NUM_BYTES_INT
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_INT
init|=
literal|4
decl_stmt|;
DECL|field|NUM_BYTES_FLOAT
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_FLOAT
init|=
literal|4
decl_stmt|;
DECL|field|NUM_BYTES_LONG
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_LONG
init|=
literal|8
decl_stmt|;
DECL|field|NUM_BYTES_DOUBLE
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_DOUBLE
init|=
literal|8
decl_stmt|;
comment|/**     * Number of bytes this jvm uses to represent an object reference.     */
DECL|field|NUM_BYTES_OBJECT_REF
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_OBJECT_REF
decl_stmt|;
comment|/**    * Number of bytes to represent an object header (no fields, no alignments).    */
DECL|field|NUM_BYTES_OBJECT_HEADER
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_OBJECT_HEADER
decl_stmt|;
comment|/**    * Number of bytes to represent an array header (no content, but with alignments).    */
DECL|field|NUM_BYTES_ARRAY_HEADER
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_ARRAY_HEADER
decl_stmt|;
comment|/**    * A constant specifying the object alignment boundary inside the JVM. Objects will    * always take a full multiple of this constant, possibly wasting some space.     */
DECL|field|NUM_BYTES_OBJECT_ALIGNMENT
specifier|public
specifier|final
specifier|static
name|int
name|NUM_BYTES_OBJECT_ALIGNMENT
decl_stmt|;
comment|/**    * Sizes of primitive classes.    */
DECL|field|primitiveSizes
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Integer
argument_list|>
name|primitiveSizes
decl_stmt|;
static|static
block|{
name|primitiveSizes
operator|=
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|boolean
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|NUM_BYTES_BOOLEAN
argument_list|)
argument_list|)
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|byte
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|NUM_BYTES_BYTE
argument_list|)
argument_list|)
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|char
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|NUM_BYTES_CHAR
argument_list|)
argument_list|)
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|short
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|NUM_BYTES_SHORT
argument_list|)
argument_list|)
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|int
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|NUM_BYTES_INT
argument_list|)
argument_list|)
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|float
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|NUM_BYTES_FLOAT
argument_list|)
argument_list|)
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|double
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|NUM_BYTES_DOUBLE
argument_list|)
argument_list|)
expr_stmt|;
name|primitiveSizes
operator|.
name|put
argument_list|(
name|long
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|NUM_BYTES_LONG
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * A handle to<code>sun.misc.Unsafe</code>.    */
DECL|field|theUnsafe
specifier|private
specifier|final
specifier|static
name|Object
name|theUnsafe
decl_stmt|;
comment|/**    * A handle to<code>sun.misc.Unsafe#fieldOffset(Field)</code>.    */
DECL|field|objectFieldOffsetMethod
specifier|private
specifier|final
specifier|static
name|Method
name|objectFieldOffsetMethod
decl_stmt|;
comment|/**    * All the supported "internal" JVM features detected at clinit.     */
DECL|field|supportedFeatures
specifier|private
specifier|final
specifier|static
name|EnumSet
argument_list|<
name|JvmFeature
argument_list|>
name|supportedFeatures
decl_stmt|;
comment|/**    * JVMs typically cache small longs. This tries to find out what the range is.    */
DECL|field|LONG_CACHE_MIN_VALUE
DECL|field|LONG_CACHE_MAX_VALUE
specifier|private
specifier|static
specifier|final
name|long
name|LONG_CACHE_MIN_VALUE
decl_stmt|,
name|LONG_CACHE_MAX_VALUE
decl_stmt|;
DECL|field|LONG_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|LONG_SIZE
decl_stmt|;
comment|/**    * Initialize constants and try to collect information about the JVM internals.     */
static|static
block|{
comment|// Initialize empirically measured defaults. We'll modify them to the current
comment|// JVM settings later on if possible.
name|int
name|referenceSize
init|=
name|Constants
operator|.
name|JRE_IS_64BIT
condition|?
literal|8
else|:
literal|4
decl_stmt|;
name|int
name|objectHeader
init|=
name|Constants
operator|.
name|JRE_IS_64BIT
condition|?
literal|16
else|:
literal|8
decl_stmt|;
comment|// The following is objectHeader + NUM_BYTES_INT, but aligned (object alignment)
comment|// so on 64 bit JVMs it'll be align(16 + 4, @8) = 24.
name|int
name|arrayHeader
init|=
name|Constants
operator|.
name|JRE_IS_64BIT
condition|?
literal|24
else|:
literal|12
decl_stmt|;
name|supportedFeatures
operator|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|JvmFeature
operator|.
name|class
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|unsafeClass
init|=
literal|null
decl_stmt|;
name|Object
name|tempTheUnsafe
init|=
literal|null
decl_stmt|;
try|try
block|{
name|unsafeClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
literal|"sun.misc.Unsafe"
argument_list|)
expr_stmt|;
specifier|final
name|Field
name|unsafeField
init|=
name|unsafeClass
operator|.
name|getDeclaredField
argument_list|(
literal|"theUnsafe"
argument_list|)
decl_stmt|;
name|unsafeField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tempTheUnsafe
operator|=
name|unsafeField
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Ignore.
block|}
name|theUnsafe
operator|=
name|tempTheUnsafe
expr_stmt|;
comment|// get object reference size by getting scale factor of Object[] arrays:
try|try
block|{
specifier|final
name|Method
name|arrayIndexScaleM
init|=
name|unsafeClass
operator|.
name|getMethod
argument_list|(
literal|"arrayIndexScale"
argument_list|,
name|Class
operator|.
name|class
argument_list|)
decl_stmt|;
name|referenceSize
operator|=
operator|(
operator|(
name|Number
operator|)
name|arrayIndexScaleM
operator|.
name|invoke
argument_list|(
name|theUnsafe
argument_list|,
name|Object
index|[]
operator|.
expr|class
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|supportedFeatures
operator|.
name|add
argument_list|(
name|JvmFeature
operator|.
name|OBJECT_REFERENCE_SIZE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore.
block|}
comment|// "best guess" based on reference size. We will attempt to modify
comment|// these to exact values if there is supported infrastructure.
name|objectHeader
operator|=
name|Constants
operator|.
name|JRE_IS_64BIT
condition|?
operator|(
literal|8
operator|+
name|referenceSize
operator|)
else|:
literal|8
expr_stmt|;
name|arrayHeader
operator|=
name|Constants
operator|.
name|JRE_IS_64BIT
condition|?
operator|(
literal|8
operator|+
literal|2
operator|*
name|referenceSize
operator|)
else|:
literal|12
expr_stmt|;
comment|// get the object header size:
comment|// - first try out if the field offsets are not scaled (see warning in Unsafe docs)
comment|// - get the object header size by getting the field offset of the first field of a dummy object
comment|// If the scaling is byte-wise and unsafe is available, enable dynamic size measurement for
comment|// estimateRamUsage().
name|Method
name|tempObjectFieldOffsetMethod
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|Method
name|objectFieldOffsetM
init|=
name|unsafeClass
operator|.
name|getMethod
argument_list|(
literal|"objectFieldOffset"
argument_list|,
name|Field
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Field
name|dummy1Field
init|=
name|DummyTwoLongObject
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"dummy1"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|ofs1
init|=
operator|(
operator|(
name|Number
operator|)
name|objectFieldOffsetM
operator|.
name|invoke
argument_list|(
name|theUnsafe
argument_list|,
name|dummy1Field
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
specifier|final
name|Field
name|dummy2Field
init|=
name|DummyTwoLongObject
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"dummy2"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|ofs2
init|=
operator|(
operator|(
name|Number
operator|)
name|objectFieldOffsetM
operator|.
name|invoke
argument_list|(
name|theUnsafe
argument_list|,
name|dummy2Field
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|ofs2
operator|-
name|ofs1
argument_list|)
operator|==
name|NUM_BYTES_LONG
condition|)
block|{
specifier|final
name|Field
name|baseField
init|=
name|DummyOneFieldObject
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"base"
argument_list|)
decl_stmt|;
name|objectHeader
operator|=
operator|(
operator|(
name|Number
operator|)
name|objectFieldOffsetM
operator|.
name|invoke
argument_list|(
name|theUnsafe
argument_list|,
name|baseField
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|supportedFeatures
operator|.
name|add
argument_list|(
name|JvmFeature
operator|.
name|FIELD_OFFSETS
argument_list|)
expr_stmt|;
name|tempObjectFieldOffsetMethod
operator|=
name|objectFieldOffsetM
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Ignore.
block|}
name|objectFieldOffsetMethod
operator|=
name|tempObjectFieldOffsetMethod
expr_stmt|;
comment|// Get the array header size by retrieving the array base offset
comment|// (offset of the first element of an array).
try|try
block|{
specifier|final
name|Method
name|arrayBaseOffsetM
init|=
name|unsafeClass
operator|.
name|getMethod
argument_list|(
literal|"arrayBaseOffset"
argument_list|,
name|Class
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// we calculate that only for byte[] arrays, it's actually the same for all types:
name|arrayHeader
operator|=
operator|(
operator|(
name|Number
operator|)
name|arrayBaseOffsetM
operator|.
name|invoke
argument_list|(
name|theUnsafe
argument_list|,
name|byte
index|[]
operator|.
expr|class
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|supportedFeatures
operator|.
name|add
argument_list|(
name|JvmFeature
operator|.
name|ARRAY_HEADER_SIZE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Ignore.
block|}
name|NUM_BYTES_OBJECT_REF
operator|=
name|referenceSize
expr_stmt|;
name|NUM_BYTES_OBJECT_HEADER
operator|=
name|objectHeader
expr_stmt|;
name|NUM_BYTES_ARRAY_HEADER
operator|=
name|arrayHeader
expr_stmt|;
comment|// Try to get the object alignment (the default seems to be 8 on Hotspot,
comment|// regardless of the architecture).
name|int
name|objectAlignment
init|=
literal|8
decl_stmt|;
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|PlatformManagedObject
argument_list|>
name|beanClazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"com.sun.management.HotSpotDiagnosticMXBean"
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|PlatformManagedObject
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|hotSpotBean
init|=
name|ManagementFactory
operator|.
name|getPlatformMXBean
argument_list|(
name|beanClazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|hotSpotBean
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Method
name|getVMOptionMethod
init|=
name|beanClazz
operator|.
name|getMethod
argument_list|(
literal|"getVMOption"
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|vmOption
init|=
name|getVMOptionMethod
operator|.
name|invoke
argument_list|(
name|hotSpotBean
argument_list|,
literal|"ObjectAlignmentInBytes"
argument_list|)
decl_stmt|;
name|objectAlignment
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|vmOption
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"getValue"
argument_list|)
operator|.
name|invoke
argument_list|(
name|vmOption
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|supportedFeatures
operator|.
name|add
argument_list|(
name|JvmFeature
operator|.
name|OBJECT_ALIGNMENT
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Ignore.
block|}
name|NUM_BYTES_OBJECT_ALIGNMENT
operator|=
name|objectAlignment
expr_stmt|;
name|JVM_INFO_STRING
operator|=
literal|"[JVM: "
operator|+
name|Constants
operator|.
name|JVM_NAME
operator|+
literal|", "
operator|+
name|Constants
operator|.
name|JVM_VERSION
operator|+
literal|", "
operator|+
name|Constants
operator|.
name|JVM_VENDOR
operator|+
literal|", "
operator|+
name|Constants
operator|.
name|JAVA_VENDOR
operator|+
literal|", "
operator|+
name|Constants
operator|.
name|JAVA_VERSION
operator|+
literal|"]"
expr_stmt|;
name|long
name|longCacheMinValue
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|longCacheMinValue
operator|>
name|Long
operator|.
name|MIN_VALUE
operator|&&
name|Long
operator|.
name|valueOf
argument_list|(
name|longCacheMinValue
operator|-
literal|1
argument_list|)
operator|==
name|Long
operator|.
name|valueOf
argument_list|(
name|longCacheMinValue
operator|-
literal|1
argument_list|)
condition|)
block|{
name|longCacheMinValue
operator|-=
literal|1
expr_stmt|;
block|}
name|long
name|longCacheMaxValue
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|longCacheMaxValue
operator|<
name|Long
operator|.
name|MAX_VALUE
operator|&&
name|Long
operator|.
name|valueOf
argument_list|(
name|longCacheMaxValue
operator|+
literal|1
argument_list|)
operator|==
name|Long
operator|.
name|valueOf
argument_list|(
name|longCacheMaxValue
operator|+
literal|1
argument_list|)
condition|)
block|{
name|longCacheMaxValue
operator|+=
literal|1
expr_stmt|;
block|}
name|LONG_CACHE_MIN_VALUE
operator|=
name|longCacheMinValue
expr_stmt|;
name|LONG_CACHE_MAX_VALUE
operator|=
name|longCacheMaxValue
expr_stmt|;
name|LONG_SIZE
operator|=
operator|(
name|int
operator|)
name|shallowSizeOfInstance
argument_list|(
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|// Object with just one field to determine the object header size by getting the offset of the dummy field:
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|class|DummyOneFieldObject
specifier|private
specifier|static
specifier|final
class|class
name|DummyOneFieldObject
block|{
DECL|field|base
specifier|public
name|byte
name|base
decl_stmt|;
block|}
comment|// Another test object for checking, if the difference in offsets of dummy1 and dummy2 is 8 bytes.
comment|// Only then we can be sure that those are real, unscaled offsets:
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|class|DummyTwoLongObject
specifier|private
specifier|static
specifier|final
class|class
name|DummyTwoLongObject
block|{
DECL|field|dummy1
DECL|field|dummy2
specifier|public
name|long
name|dummy1
decl_stmt|,
name|dummy2
decl_stmt|;
block|}
comment|/**     * Returns true, if the current JVM is fully supported by {@code RamUsageEstimator}.    * If this method returns {@code false} you are maybe using a 3rd party Java VM    * that is not supporting Oracle/Sun private APIs. The memory estimates can be     * imprecise then (no way of detecting compressed references, alignments, etc.).     * Lucene still tries to use sensible defaults.    */
DECL|method|isSupportedJVM
specifier|public
specifier|static
name|boolean
name|isSupportedJVM
parameter_list|()
block|{
return|return
name|supportedFeatures
operator|.
name|size
argument_list|()
operator|==
name|JvmFeature
operator|.
name|values
argument_list|()
operator|.
name|length
return|;
block|}
comment|/**     * Aligns an object size to be the next multiple of {@link #NUM_BYTES_OBJECT_ALIGNMENT}.     */
DECL|method|alignObjectSize
specifier|public
specifier|static
name|long
name|alignObjectSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|size
operator|+=
operator|(
name|long
operator|)
name|NUM_BYTES_OBJECT_ALIGNMENT
operator|-
literal|1L
expr_stmt|;
return|return
name|size
operator|-
operator|(
name|size
operator|%
name|NUM_BYTES_OBJECT_ALIGNMENT
operator|)
return|;
block|}
comment|/**    * Return the size of the provided {@link Long} object, returning 0 if it is    * cached by the JVM and its shallow size otherwise.    */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|Long
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|>=
name|LONG_CACHE_MIN_VALUE
operator|&&
name|value
operator|<=
name|LONG_CACHE_MAX_VALUE
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|LONG_SIZE
return|;
block|}
comment|/** Returns the size in bytes of the byte[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|byte
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the size in bytes of the boolean[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|boolean
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the size in bytes of the char[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|char
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|NUM_BYTES_CHAR
operator|*
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the size in bytes of the short[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|short
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|NUM_BYTES_SHORT
operator|*
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the size in bytes of the int[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|int
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|NUM_BYTES_INT
operator|*
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the size in bytes of the float[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|float
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|NUM_BYTES_FLOAT
operator|*
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the size in bytes of the long[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|long
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|NUM_BYTES_LONG
operator|*
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the size in bytes of the double[] object. */
DECL|method|sizeOf
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|double
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|NUM_BYTES_DOUBLE
operator|*
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns the shallow size in bytes of the Object[] object. */
comment|// Use this method instead of #shallowSizeOf(Object) to avoid costly reflection
DECL|method|shallowSizeOf
specifier|public
specifier|static
name|long
name|shallowSizeOf
parameter_list|(
name|Object
index|[]
name|arr
parameter_list|)
block|{
return|return
name|alignObjectSize
argument_list|(
operator|(
name|long
operator|)
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|NUM_BYTES_OBJECT_REF
operator|*
name|arr
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**     * Estimates a "shallow" memory usage of the given object. For arrays, this will be the    * memory taken by array storage (no subreferences will be followed). For objects, this    * will be the memory taken by the fields.    *     * JVM object alignments are also applied.    */
DECL|method|shallowSizeOf
specifier|public
specifier|static
name|long
name|shallowSizeOf
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|0
return|;
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clz
init|=
name|obj
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|clz
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|shallowSizeOfArray
argument_list|(
name|obj
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|shallowSizeOfInstance
argument_list|(
name|clz
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns the shallow instance size in bytes an instance of the given class would occupy.    * This works with all conventional classes and primitive types, but not with arrays    * (the size then depends on the number of elements and varies from object to object).    *     * @see #shallowSizeOf(Object)    * @throws IllegalArgumentException if {@code clazz} is an array class.     */
DECL|method|shallowSizeOfInstance
specifier|public
specifier|static
name|long
name|shallowSizeOfInstance
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|.
name|isArray
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"This method does not work with array classes."
argument_list|)
throw|;
if|if
condition|(
name|clazz
operator|.
name|isPrimitive
argument_list|()
condition|)
return|return
name|primitiveSizes
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
return|;
name|long
name|size
init|=
name|NUM_BYTES_OBJECT_HEADER
decl_stmt|;
comment|// Walk type hierarchy
for|for
control|(
init|;
name|clazz
operator|!=
literal|null
condition|;
name|clazz
operator|=
name|clazz
operator|.
name|getSuperclass
argument_list|()
control|)
block|{
specifier|final
name|Field
index|[]
name|fields
init|=
name|clazz
operator|.
name|getDeclaredFields
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|f
range|:
name|fields
control|)
block|{
if|if
condition|(
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|f
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
name|size
operator|=
name|adjustForField
argument_list|(
name|size
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|alignObjectSize
argument_list|(
name|size
argument_list|)
return|;
block|}
comment|/**    * Return shallow size of any<code>array</code>.    */
DECL|method|shallowSizeOfArray
specifier|private
specifier|static
name|long
name|shallowSizeOfArray
parameter_list|(
name|Object
name|array
parameter_list|)
block|{
name|long
name|size
init|=
name|NUM_BYTES_ARRAY_HEADER
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|Array
operator|.
name|getLength
argument_list|(
name|array
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|arrayElementClazz
init|=
name|array
operator|.
name|getClass
argument_list|()
operator|.
name|getComponentType
argument_list|()
decl_stmt|;
if|if
condition|(
name|arrayElementClazz
operator|.
name|isPrimitive
argument_list|()
condition|)
block|{
name|size
operator|+=
operator|(
name|long
operator|)
name|len
operator|*
name|primitiveSizes
operator|.
name|get
argument_list|(
name|arrayElementClazz
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|size
operator|+=
operator|(
name|long
operator|)
name|NUM_BYTES_OBJECT_REF
operator|*
name|len
expr_stmt|;
block|}
block|}
return|return
name|alignObjectSize
argument_list|(
name|size
argument_list|)
return|;
block|}
comment|/**    * This method returns the maximum representation size of an object.<code>sizeSoFar</code>    * is the object's size measured so far.<code>f</code> is the field being probed.    *     *<p>The returned offset will be the maximum of whatever was measured so far and     *<code>f</code> field's offset and representation size (unaligned).    */
DECL|method|adjustForField
specifier|static
name|long
name|adjustForField
parameter_list|(
name|long
name|sizeSoFar
parameter_list|,
specifier|final
name|Field
name|f
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|type
init|=
name|f
operator|.
name|getType
argument_list|()
decl_stmt|;
specifier|final
name|int
name|fsize
init|=
name|type
operator|.
name|isPrimitive
argument_list|()
condition|?
name|primitiveSizes
operator|.
name|get
argument_list|(
name|type
argument_list|)
else|:
name|NUM_BYTES_OBJECT_REF
decl_stmt|;
if|if
condition|(
name|objectFieldOffsetMethod
operator|!=
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|long
name|offsetPlusSize
init|=
operator|(
operator|(
name|Number
operator|)
name|objectFieldOffsetMethod
operator|.
name|invoke
argument_list|(
name|theUnsafe
argument_list|,
name|f
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
operator|+
name|fsize
decl_stmt|;
return|return
name|Math
operator|.
name|max
argument_list|(
name|sizeSoFar
argument_list|,
name|offsetPlusSize
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Access problem with sun.misc.Unsafe"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|ite
parameter_list|)
block|{
specifier|final
name|Throwable
name|cause
init|=
name|ite
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|RuntimeException
condition|)
throw|throw
operator|(
name|RuntimeException
operator|)
name|cause
throw|;
if|if
condition|(
name|cause
operator|instanceof
name|Error
condition|)
throw|throw
operator|(
name|Error
operator|)
name|cause
throw|;
comment|// this should never happen (Unsafe does not declare
comment|// checked Exceptions for this method), but who knows?
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Call to Unsafe's objectFieldOffset() throwed "
operator|+
literal|"checked Exception when accessing field "
operator|+
name|f
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"#"
operator|+
name|f
operator|.
name|getName
argument_list|()
argument_list|,
name|cause
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// TODO: No alignments based on field type/ subclass fields alignments?
return|return
name|sizeSoFar
operator|+
name|fsize
return|;
block|}
block|}
comment|/** Return the set of unsupported JVM features that improve the estimation. */
DECL|method|getUnsupportedFeatures
specifier|public
specifier|static
name|EnumSet
argument_list|<
name|JvmFeature
argument_list|>
name|getUnsupportedFeatures
parameter_list|()
block|{
name|EnumSet
argument_list|<
name|JvmFeature
argument_list|>
name|unsupported
init|=
name|EnumSet
operator|.
name|allOf
argument_list|(
name|JvmFeature
operator|.
name|class
argument_list|)
decl_stmt|;
name|unsupported
operator|.
name|removeAll
argument_list|(
name|supportedFeatures
argument_list|)
expr_stmt|;
return|return
name|unsupported
return|;
block|}
comment|/** Return the set of supported JVM features that improve the estimation. */
DECL|method|getSupportedFeatures
specifier|public
specifier|static
name|EnumSet
argument_list|<
name|JvmFeature
argument_list|>
name|getSupportedFeatures
parameter_list|()
block|{
return|return
name|EnumSet
operator|.
name|copyOf
argument_list|(
name|supportedFeatures
argument_list|)
return|;
block|}
comment|/**    * Returns<code>size</code> in human-readable units (GB, MB, KB or bytes).    */
DECL|method|humanReadableUnits
specifier|public
specifier|static
name|String
name|humanReadableUnits
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
return|return
name|humanReadableUnits
argument_list|(
name|bytes
argument_list|,
operator|new
name|DecimalFormat
argument_list|(
literal|"0.#"
argument_list|,
name|DecimalFormatSymbols
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns<code>size</code> in human-readable units (GB, MB, KB or bytes).     */
DECL|method|humanReadableUnits
specifier|public
specifier|static
name|String
name|humanReadableUnits
parameter_list|(
name|long
name|bytes
parameter_list|,
name|DecimalFormat
name|df
parameter_list|)
block|{
if|if
condition|(
name|bytes
operator|/
name|ONE_GB
operator|>
literal|0
condition|)
block|{
return|return
name|df
operator|.
name|format
argument_list|(
operator|(
name|float
operator|)
name|bytes
operator|/
name|ONE_GB
argument_list|)
operator|+
literal|" GB"
return|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|/
name|ONE_MB
operator|>
literal|0
condition|)
block|{
return|return
name|df
operator|.
name|format
argument_list|(
operator|(
name|float
operator|)
name|bytes
operator|/
name|ONE_MB
argument_list|)
operator|+
literal|" MB"
return|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|/
name|ONE_KB
operator|>
literal|0
condition|)
block|{
return|return
name|df
operator|.
name|format
argument_list|(
operator|(
name|float
operator|)
name|bytes
operator|/
name|ONE_KB
argument_list|)
operator|+
literal|" KB"
return|;
block|}
else|else
block|{
return|return
name|bytes
operator|+
literal|" bytes"
return|;
block|}
block|}
block|}
end_class

end_unit

