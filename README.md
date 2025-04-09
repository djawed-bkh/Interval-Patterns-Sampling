# Repository Overview

This repository contains all the necessary components to evaluate **FIPS** and **HFIPS** — two approaches for sampling interval patterns from numerical data, proportionally to frequency (FIPS) and to the product of hyper-volume and frequency (HFIPS).

## Contents

- **Source code for FIPS**  
  Implementation of the FIPS sampling method.  
  *(See the `src` directory)*

- **Source code for HFIPS**  
  Implementation of the HFIPS sampling method.  
  *(See the `src` directory)*

- **Source code for uniform sampling (with coverage)**  
  A method for uniformly sampling interval patterns while ensuring non-empty coverage.  
  *(See the `src` directory)*

- **Source code for uniform sampling (without coverage)**  
  A method for uniform sampling of interval patterns without coverage guarantees.  
  *(See the `src` directory)*

- **Source code for running experiments**  
  All experimental evaluations are implemented in the `Evaluation` class.  
  To run a specific evaluation, uncomment the corresponding method call in the `Main` class.  
  *(See the `src` directory)*

- **Benchmark datasets**  
  Datasets used in the experimental protocol.  
  *(See the `benchmark` directory)*

- **Graphical results**  
  Visualizations of experimental results for each dataset.  
  *(See the `result` directory)*
