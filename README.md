# Data Reader

![](https://github.com/Metastring/data-reader/workflows/Build/badge.svg)

This package allows one to read data into multidimensional data from partially structured tabular form.

## What it does

Imagine you have a CSV file which looks roughly like this

```csv
State,District,Maternal Mortality Rate - Urban,Maternal Mortality Rate - Rural,Infant Mortality Rate
Karnataka,Bangalore Urban,1.3,NA,0.5
Karnataka,Mysore,1.5,1.6,0.4
```

When this CSV is read with the correct metadata, this library can produce maps like this:

```json
[
    {
       "entity.state": "Karnataka",
       "entity.district": "Bangalore Urban",
       "indicator": "Maternal Mortality Rate",
       "settlement": "Urban",
       "value": "1.3"
    },
    {
       "entity.state": "Karnataka",
       "entity.district": "Bangalore Urban",
       "indicator": "Maternal Mortality Rate",
       "settlement": "Rural",
       "value": "NA"
    },
    {
       "entity.state": "Karnataka",
       "entity.district": "Bangalore Urban",
       "indicator": "Infant Mortality Rate",
       "value": "0.5"
    },
    {
       "entity.state": "Karnataka",
       "entity.district": "Mysore",
       "indicator": "Maternal Mortality Rate",
       "settlement": "Urban",
       "value": "1.5"
    },
    {
       "entity.state": "Karnataka",
       "entity.district": "Mysore",
       "indicator": "Maternal Mortality Rate",
       "settlement": "Rural",
       "value": "1.6"
    },
    {
       "entity.state": "Karnataka",
       "entity.district": "Mysore",
       "indicator": "Infant Mortality Rate",
       "value": "0.4"
    }
]
```

The data points thus produced can be used for further analytics.

The metadata that accomplishes the above might look like

```json
{
  "fields": [
    {
      "field": "entity.state",
      "range": "A2:A"
    },
    {
      "field": "entity.district",
      "range": "B2:B"
    },
    {
      "field": "indicator",
      "patterns": [
        {
          "range": "C1:D1",
          "pattern": "(.*) - .*"
        },
        {
          "range": "E1"
        }
      ]
    },
    {
      "field": "settlement",
      "range": "C1:D1",
      "pattern": ".* - (.*)"
    },
    {
      "field": "value",
      "range": "C2:"
    }
  ]
}
```

## How to use

At the moment, this library is not designed for public use and the API and metadata schema may change at any time.

If you nevertheless want to test it, or you are trying to contribute to the [health-heatmap-backend](https://github.com/Metastring/health-heatmap-backend), you can see how this library is used in the [core](https://github.com/Metastring/health-heatmap-backend/tree/main/core) module under ETL.

## Feedback

You can use the issues section of this repository for any feedback/issues/suggestions/bug reports.

## Roadmap

- [ ] versatile metadata (Taking lessons from Apache Druid) - https://github.com/Metastring/HealthHeatMap/issues/29
- [ ] JSON support