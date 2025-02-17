-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 17-02-2025 a las 00:00:10
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `gestort8`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categorias_tbl`
--

CREATE TABLE `categorias_tbl` (
  `idCategoria` int(11) NOT NULL,
  `sNombre` varchar(64) NOT NULL,
  `sDescripcion` varchar(256) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `categorias_tbl`
--

INSERT INTO `categorias_tbl` (`idCategoria`, `sNombre`, `sDescripcion`) VALUES
(1, 'CPU', 'Procesador'),
(2, 'GPU', 'Tarjeta Grafica');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos_tbl`
--

CREATE TABLE `productos_tbl` (
  `idProducto` int(11) NOT NULL,
  `sNombre` varchar(128) NOT NULL,
  `sDescripcion` text DEFAULT NULL,
  `dPrecio` decimal(10,2) DEFAULT NULL,
  `iCodigoProducto` bigint(20) DEFAULT NULL,
  `idCategoria` int(11) NOT NULL,
  `bActivo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `productos_tbl`
--

INSERT INTO `productos_tbl` (`idProducto`, `sNombre`, `sDescripcion`, `dPrecio`, `iCodigoProducto`, `idCategoria`, `bActivo`) VALUES
(1, 'Test1', 'Descripcion', 800.00, 4001, 1, 1),
(2, 'Test2', 'Descripcion', 600.00, 4002, 1, 1),
(3, 'Test3', 'Desc', 500.60, 4003, 1, 1),
(4, 'Test4', 'Desc', 800.00, 4004, 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `stock_tbl`
--

CREATE TABLE `stock_tbl` (
  `idStock` int(11) NOT NULL,
  `idProducto` int(11) NOT NULL,
  `iCantidad` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `stock_tbl`
--

INSERT INTO `stock_tbl` (`idStock`, `idProducto`, `iCantidad`) VALUES
(1, 1, 0),
(2, 2, 0),
(6, 6, 0),
(7, 3, 0),
(8, 5, 0),
(9, 4, 0);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `categorias_tbl`
--
ALTER TABLE `categorias_tbl`
  ADD PRIMARY KEY (`idCategoria`);

--
-- Indices de la tabla `productos_tbl`
--
ALTER TABLE `productos_tbl`
  ADD PRIMARY KEY (`idProducto`),
  ADD UNIQUE KEY `iCodigoProducto` (`iCodigoProducto`);

--
-- Indices de la tabla `stock_tbl`
--
ALTER TABLE `stock_tbl`
  ADD PRIMARY KEY (`idStock`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `categorias_tbl`
--
ALTER TABLE `categorias_tbl`
  MODIFY `idCategoria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `productos_tbl`
--
ALTER TABLE `productos_tbl`
  MODIFY `idProducto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `stock_tbl`
--
ALTER TABLE `stock_tbl`
  MODIFY `idStock` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
