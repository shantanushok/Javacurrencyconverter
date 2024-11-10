package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONObject;

public class CurrencyConverter extends JFrame {
    private JTextField amountField;
    private JComboBox<String> fromCurrencyBox;
    private JComboBox<String> toCurrencyBox;
    private JLabel resultLabel;
    private JLabel amountLabel, fromLabel, toLabel; // Labels as instance variables
    private Map<String, Double> rates;

    public CurrencyConverter() {
        setTitle("Currency Converter");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set a larger default font for all components
        Font defaultFont = new Font("Arial", Font.PLAIN, 16);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);

        // Define Colors
        Color backgroundColor = new Color(245, 245, 245); // Light gray for background
        Color primaryTextColor = new Color(33, 33, 33);   // Dark gray for primary text
        Color accentColor = new Color(70, 130, 180);      // Blue accent for buttons
        Color highlightColor = new Color(0, 102, 51);     // Dark green for results

        // Main panel setup with background color and padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(backgroundColor);  // Set background color
        add(mainPanel);

        // Title label setup with larger font and primary color
        JLabel titleLabel = new JLabel("Currency Converter");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(primaryTextColor);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Initialize labels
        // Define a larger font for labels
        Font labelFont = new Font("Arial", Font.BOLD, 18); // Set to 18pt, bold

        // Initialize amountLabel with custom font size and color
        amountLabel = new JLabel("Amount:");
        amountLabel.setForeground(primaryTextColor);
        amountLabel.setFont(labelFont);  // Apply the custom font

        // Initialize fromLabel with custom font size and color
        fromLabel = new JLabel("From:");
        fromLabel.setForeground(primaryTextColor);
        fromLabel.setFont(labelFont);  // Apply the custom font

        // Initialize toLabel with custom font size and color
        toLabel = new JLabel("To:");
        toLabel.setForeground(primaryTextColor);
        toLabel.setFont(labelFont);  // Apply the custom font


        // Component setup with color and font adjustments
        amountField = new JTextField(10);
        amountField.setFont(defaultFont);
        amountField.setBackground(Color.WHITE);        // White background for clarity

        fromCurrencyBox = new JComboBox<>();
        fromCurrencyBox.setFont(defaultFont);
        fromCurrencyBox.setBackground(Color.WHITE);    // White background for contrast

        toCurrencyBox = new JComboBox<>();
        toCurrencyBox.setFont(defaultFont);
        toCurrencyBox.setBackground(Color.WHITE);      // White background for contrast

        JButton convertButton = new JButton("Convert");
        convertButton.setFont(defaultFont);
        convertButton.setBackground(accentColor);      // Accent color for button
        convertButton.setForeground(Color.WHITE);      // White text on button for contrast

        resultLabel = new JLabel("Converted amount will be shown here");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setForeground(highlightColor);     // Highlight color for result

        // Layout manager setup with larger spacing
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 12);       // Increased spacing
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        mainPanel.add(amountField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(fromLabel, gbc);

        gbc.gridx = 1;
        mainPanel.add(fromCurrencyBox, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(toLabel, gbc);

        gbc.gridx = 1;
        mainPanel.add(toCurrencyBox, gbc);

        gbc.gridy = 4;
        gbc.gridx = 1;
        mainPanel.add(convertButton, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(resultLabel, gbc);

        // Fetch exchange rates from the API
        fetchRates();

        // Add ActionListener for the conversion button
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleConvert();
            }
        });
    }

    private void fetchRates() {
        try {
            String apiUrl = "https://open.er-api.com/v6/latest/USD";
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject ratesJson = jsonResponse.getJSONObject("rates");
            rates = new TreeMap<>();
            for (String key : ratesJson.keySet()) {
                rates.put(key, ratesJson.getDouble(key));
            }

            for (String currency : rates.keySet()) {
                fromCurrencyBox.addItem(currency);
                toCurrencyBox.addItem(currency);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch exchange rates", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleConvert() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String fromCurrency = (String) fromCurrencyBox.getSelectedItem();
            String toCurrency = (String) toCurrencyBox.getSelectedItem();
            double fromRate = rates.get(fromCurrency);
            double toRate = rates.get(toCurrency);
            double convertedAmount = (amount / fromRate) * toRate;
            resultLabel.setText(String.format("%.2f %s = %.2f %s", amount, fromCurrency, convertedAmount, toCurrency));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CurrencyConverter().setVisible(true));
    }
}

