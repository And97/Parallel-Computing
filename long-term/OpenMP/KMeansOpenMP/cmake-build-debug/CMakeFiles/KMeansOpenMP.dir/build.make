# CMAKE generated file: DO NOT EDIT!
# Generated by "MinGW Makefiles" Generator, CMake Version 3.17

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Disable VCS-based implicit rules.
% : %,v


# Disable VCS-based implicit rules.
% : RCS/%


# Disable VCS-based implicit rules.
% : RCS/%,v


# Disable VCS-based implicit rules.
% : SCCS/s.%


# Disable VCS-based implicit rules.
% : s.%


.SUFFIXES: .hpux_make_needs_suffix_list


# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

SHELL = cmd.exe

# The CMake executable.
CMAKE_COMMAND = "C:\Program Files\JetBrains\CLion 2020.3.3\bin\cmake\win\bin\cmake.exe"

# The command to remove a file.
RM = "C:\Program Files\JetBrains\CLion 2020.3.3\bin\cmake\win\bin\cmake.exe" -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = C:\Users\Andrea\CLionProjects\KMeansOpenMP

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = C:\Users\Andrea\CLionProjects\KMeansOpenMP\cmake-build-debug

# Include any dependencies generated for this target.
include CMakeFiles/KMeansOpenMP.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/KMeansOpenMP.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/KMeansOpenMP.dir/flags.make

CMakeFiles/KMeansOpenMP.dir/main.cpp.obj: CMakeFiles/KMeansOpenMP.dir/flags.make
CMakeFiles/KMeansOpenMP.dir/main.cpp.obj: ../main.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=C:\Users\Andrea\CLionProjects\KMeansOpenMP\cmake-build-debug\CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/KMeansOpenMP.dir/main.cpp.obj"
	C:\PROGRA~1\MINGW-~1\X86_64~1.0-P\mingw64\bin\G__~1.EXE  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles\KMeansOpenMP.dir\main.cpp.obj -c C:\Users\Andrea\CLionProjects\KMeansOpenMP\main.cpp

CMakeFiles/KMeansOpenMP.dir/main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/KMeansOpenMP.dir/main.cpp.i"
	C:\PROGRA~1\MINGW-~1\X86_64~1.0-P\mingw64\bin\G__~1.EXE $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E C:\Users\Andrea\CLionProjects\KMeansOpenMP\main.cpp > CMakeFiles\KMeansOpenMP.dir\main.cpp.i

CMakeFiles/KMeansOpenMP.dir/main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/KMeansOpenMP.dir/main.cpp.s"
	C:\PROGRA~1\MINGW-~1\X86_64~1.0-P\mingw64\bin\G__~1.EXE $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S C:\Users\Andrea\CLionProjects\KMeansOpenMP\main.cpp -o CMakeFiles\KMeansOpenMP.dir\main.cpp.s

# Object files for target KMeansOpenMP
KMeansOpenMP_OBJECTS = \
"CMakeFiles/KMeansOpenMP.dir/main.cpp.obj"

# External object files for target KMeansOpenMP
KMeansOpenMP_EXTERNAL_OBJECTS =

KMeansOpenMP.exe: CMakeFiles/KMeansOpenMP.dir/main.cpp.obj
KMeansOpenMP.exe: CMakeFiles/KMeansOpenMP.dir/build.make
KMeansOpenMP.exe: CMakeFiles/KMeansOpenMP.dir/linklibs.rsp
KMeansOpenMP.exe: CMakeFiles/KMeansOpenMP.dir/objects1.rsp
KMeansOpenMP.exe: CMakeFiles/KMeansOpenMP.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=C:\Users\Andrea\CLionProjects\KMeansOpenMP\cmake-build-debug\CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable KMeansOpenMP.exe"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles\KMeansOpenMP.dir\link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/KMeansOpenMP.dir/build: KMeansOpenMP.exe

.PHONY : CMakeFiles/KMeansOpenMP.dir/build

CMakeFiles/KMeansOpenMP.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles\KMeansOpenMP.dir\cmake_clean.cmake
.PHONY : CMakeFiles/KMeansOpenMP.dir/clean

CMakeFiles/KMeansOpenMP.dir/depend:
	$(CMAKE_COMMAND) -E cmake_depends "MinGW Makefiles" C:\Users\Andrea\CLionProjects\KMeansOpenMP C:\Users\Andrea\CLionProjects\KMeansOpenMP C:\Users\Andrea\CLionProjects\KMeansOpenMP\cmake-build-debug C:\Users\Andrea\CLionProjects\KMeansOpenMP\cmake-build-debug C:\Users\Andrea\CLionProjects\KMeansOpenMP\cmake-build-debug\CMakeFiles\KMeansOpenMP.dir\DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/KMeansOpenMP.dir/depend

