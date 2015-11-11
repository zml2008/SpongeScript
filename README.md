SpongeScript


# Loading phases
    ### Parallel
    1. Discovery: The specified paths are used to match scripts
    2. Loading: Each of these scripts is matched against a language. This language implementation is used to create a CompiledScript.
    ### Sequential
    3. Execution: each of the scripts is enabled as necessary. dependencies between scripts are calculated using the require function.


# Script Discovery

The discovery uses

# Inclusion of other scripts



