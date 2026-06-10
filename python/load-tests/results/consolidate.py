import os
import pandas as pd
import glob

def consolidate():
    results_dir = "/home/mandreamorim/IdeaProjects/testes-invoc-remota-java/python/load-tests/results"
    categories = ["stats", "failures", "stats_history", "exceptions"]
    
    for cat in categories:
        pattern = f"*_{cat}.csv"
        # Search specifically in the results directory
        files = glob.glob(os.path.join(results_dir, pattern))
        
        # Filter out already consolidated files to avoid recursion if script is re-run
        files = [f for f in files if "consolidated_" not in os.path.basename(f)]
        
        if not files:
            print(f"No files found for category: {cat}")
            continue
            
        all_dfs = []
        for file in files:
            filename = os.path.basename(file)
            # Pattern expected: [language]_[protocol]_[intensity]_[category].csv
            # e.g., java_graphql_leve_stats.csv
            
            # Remove the category suffice to get the config part
            config_str = filename.replace(f"_{cat}.csv", "")
            parts = config_str.split("_")
            
            # Assign parts based on position
            lang = parts[0] if len(parts) > 0 else "N/A"
            protocol = parts[1] if len(parts) > 1 else "N/A"
            intensity = parts[2] if len(parts) > 2 else "N/A"
            
            try:
                # Use on_bad_lines='skip' or similar if Locust output is ever messy
                df = pd.read_csv(file)
                
                # Even if it's just headers, we might want to keep the info that it had no data
                # but adding columns to an empty DF with headers works fine.
                
                # Insert metadata columns at the beginning
                df.insert(0, 'Language', lang)
                df.insert(1, 'Protocol', protocol)
                df.insert(2, 'Intensity', intensity)
                
                all_dfs.append(df)
            except Exception as e:
                print(f"Error reading {file}: {e}")
        
        if all_dfs:
            combined_df = pd.concat(all_dfs, ignore_index=True)
            output_file = os.path.join(results_dir, f"consolidated_{cat}.csv")
            combined_df.to_csv(output_file, index=False)
            print(f"Successfully consolidated {len(files)} files into {output_file}")
        else:
            print(f"No data to consolidate for category: {cat}")

if __name__ == "__main__":
    consolidate()
